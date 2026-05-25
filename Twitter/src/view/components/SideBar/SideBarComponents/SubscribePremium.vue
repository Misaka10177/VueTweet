<template>
  <div id="subscribe-premium">
    <div class="title">
      <span>订阅 Premium</span>
      <span class="discounts">50% 优惠</span>
    </div>
    <div class="text">免广告、查看你的分析数据、推广你的回复并解锁 20 多项功能。</div>
    <div class="button">
      <button>订阅</button>
    </div>
  </div>
</template>
<script setup lang="ts"></script>
<style scoped>
#subscribe-premium {
  /* width: 100%;
  border: var(--boundary-style);
  border-radius: 20px; */
  padding: 15px;
}
#subscribe-premium > div {
  margin-bottom: 15px;
}
#subscribe-premium > div:last-child {
  margin-bottom: 0;
}
.title > span {
  margin-right: 10px;
}
.title > span:first-child {
  font-size: var(--title-font-size);
  font-weight: var(--title-font-weight);
}
.title .discounts {
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  color: rgb(0, 126, 80);
  background-color: rgba(194, 241, 220, 1);
}
button {
  width: 64px;
  height: 36px;
  border-radius: 18px;
  font-weight: 500;
  color: #fff;
  background-color: var(--theme-color);
}
</style>
