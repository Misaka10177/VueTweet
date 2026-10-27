<template>
  <div class="composer">
    <div class="composer-row">
      <div class="avatar">
        <img :src="`${base}images/img.png`" alt="你的头像" />
      </div>
      <div class="main">
        <textarea
          ref="textarea"
          v-model="text"
          rows="2"
          placeholder="有什么新鲜事？"
          aria-label="帖子内容"
          @input="autoResize"
        ></textarea>

        <div v-if="previews.length" class="previews">
          <div v-for="(item, index) in previews" :key="item.url" class="preview">
            <img :src="item.url" :alt="`待上传图片 ${index + 1}`" />
            <button
              type="button"
              class="remove"
              :aria-label="`移除第 ${index + 1} 张图片`"
              @click="removeImage(index)"
            >
              ×
            </button>
          </div>
        </div>

        <div v-if="error" class="error" role="alert">{{ error }}</div>

        <div class="footer">
          <div class="tools">
            <button
              type="button"
              class="image-btn"
              aria-label="添加图片"
              title="添加图片"
              :disabled="submitting"
              @click="pickImages"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  d="M3 5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5zm2 0v9.6l4.2-4.2 3.4 3.4 2.3-2.3L19 15.6V5H5zm14 12.4-3.5-3.5-2.3 2.3-3.4-3.4L5 17.4V19h14v-1.6zM8.5 7a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3z"
                />
              </svg>
            </button>
            <span class="count" :class="{ over: remaining < 0 }">{{ remaining }}</span>
          </div>
          <button
            type="button"
            class="submit"
            :disabled="!canSubmit"
            @click="submit"
          >
            {{ submitting ? '发布中…' : '发帖' }}
          </button>
        </div>
      </div>
    </div>

    <input
      ref="fileInput"
      class="file-input"
      type="file"
      accept="image/jpeg,image/png,image/webp,image/gif"
      multiple
      @change="onFiles"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import { createTweet } from '@/request/api'
import type { Tweet } from '@/types'

const emit = defineEmits<{ (e: 'posted', tweet: Tweet): void }>()

const MAX_TEXT = 280
const MAX_IMAGES = 4
const MAX_IMAGE_SIZE = 5 * 1024 * 1024
const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif']
const base = import.meta.env.BASE_URL

const text = ref('')
const previews = ref<{ file: File; url: string }[]>([])
const submitting = ref(false)
const error = ref('')
const fileInput = ref<HTMLInputElement | null>(null)
const textarea = ref<HTMLTextAreaElement | null>(null)

const remaining = computed(() => MAX_TEXT - text.value.length)
const canSubmit = computed(
  () =>
    !submitting.value &&
    remaining.value >= 0 &&
    (text.value.trim().length > 0 || previews.value.length > 0),
)

function autoResize() {
  const el = textarea.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${Math.min(el.scrollHeight, 200)}px`
}

function pickImages() {
  if (!submitting.value) fileInput.value?.click()
}

function onFiles(event: Event) {
  const input = event.target as HTMLInputElement
  const incoming = Array.from(input.files || [])
  input.value = ''
  if (!incoming.length) return

  error.value = ''
  const slots = MAX_IMAGES - previews.value.length
  if (incoming.length > slots) {
    error.value = `最多上传 ${MAX_IMAGES} 张图片`
  }

  for (const file of incoming.slice(0, Math.max(0, slots))) {
    if (!ALLOWED_TYPES.includes(file.type)) {
      error.value = '仅支持 JPG、PNG、WebP、GIF 图片'
      continue
    }
    if (file.size > MAX_IMAGE_SIZE) {
      error.value = '单张图片不能超过 5MB'
      continue
    }
    previews.value.push({ file, url: URL.createObjectURL(file) })
  }
}

function removeImage(index: number) {
  const item = previews.value[index]
  if (item) URL.revokeObjectURL(item.url)
  previews.value.splice(index, 1)
}

function reset() {
  previews.value.forEach((item) => URL.revokeObjectURL(item.url))
  previews.value = []
  text.value = ''
  error.value = ''
  if (fileInput.value) fileInput.value.value = ''
  if (textarea.value) textarea.value.style.height = 'auto'
}

async function submit() {
  if (!canSubmit.value) return
  submitting.value = true
  error.value = ''

  const form = new FormData()
  form.append('text', text.value.trim())
  previews.value.forEach((item) => form.append('images', item.file))

  try {
    const tweet = await createTweet(form)
    if (!tweet) throw new Error('发布失败')
    emit('posted', tweet)
    reset()
  } catch (err: unknown) {
    const e = err as { message?: string } | undefined
    error.value = e?.message || '发布失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}

onBeforeUnmount(() => {
  previews.value.forEach((item) => URL.revokeObjectURL(item.url))
})
</script>

<style scoped>
.composer {
  padding: 12px 16px;
  border-bottom: 1px solid var(--boundary-style, #eff3f4);
  background: #fff;
}
.composer-row {
  display: flex;
  gap: 12px;
}
.avatar {
  width: 40px;
  height: 40px;
  flex: 0 0 40px;
  border-radius: 50%;
  overflow: hidden;
  background: #eff3f4;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.main {
  flex: 1;
  min-width: 0;
}
textarea {
  display: block;
  width: 100%;
  min-height: 52px;
  max-height: 200px;
  padding: 6px 0;
  border: 0;
  outline: none;
  resize: none;
  background: transparent;
  color: var(--black-color, #0f1419);
  font: inherit;
  font-size: 18px;
  line-height: 1.4;
}
textarea::placeholder {
  color: var(--grey-color, #536471);
}
.previews {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  margin-top: 8px;
}
.preview {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  border-radius: 12px;
  background: #eff3f4;
}
.preview img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.remove {
  position: absolute;
  top: 6px;
  right: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
}
.error {
  margin-top: 8px;
  color: #f4212e;
  font-size: 13px;
  line-height: 1.4;
}
.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 8px;
}
.tools {
  display: flex;
  align-items: center;
  gap: 6px;
}
.image-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  color: var(--theme-color, #1d9bf0);
  cursor: pointer;
}
.image-btn:hover {
  background: rgba(29, 155, 240, 0.1);
}
.image-btn svg {
  width: 20px;
  height: 20px;
  fill: currentColor;
}
.count {
  font-size: 12px;
  color: var(--grey-color, #536471);
}
.count.over {
  color: #f4212e;
}
.submit {
  min-width: 70px;
  height: 34px;
  padding: 0 16px;
  border-radius: 17px;
  background: var(--black-color, #0f1419);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.submit:disabled {
  opacity: 0.5;
  cursor: default;
}
.file-input {
  display: none;
}
</style>
