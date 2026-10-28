<template>
  <span
    v-if="!self && (following || followsYou)"
    class="relation-badge"
    :class="{ mutual: following && followsYou }"
  >
    {{ following && followsYou ? '互相关注' : '关注了你' }}
  </span>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{ following: boolean; followsYou: boolean; self?: boolean }>(),
  { self: false },
)
</script>

<style scoped>
.relation-badge {
  display: inline-block;
  flex: none;
  padding: 1px 6px;
  border-radius: 4px;
  background: #eff3f4;
  color: #536471;
  font-size: 12px;
  font-weight: 500;
  line-height: 18px;
  white-space: nowrap;
}
.relation-badge.mutual {
  color: #0f1419;
}
</style>
