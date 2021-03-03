<template>
  <div class="session-list">
    <a class="session-item pl-2 is-active">
      <figure class="image is-48x48 media-left">
        <img class="is-rounded" src="https://via.placeholder.com/100?text=KK" />
      </figure>
      <div>我是KK</div>
    </a>
    <a class="session-item pl-2">
      <figure class="image is-48x48 media-left">
        <img class="is-rounded" src="https://via.placeholder.com/100?text=KK" />
      </figure>
      <div>我是KK</div>
    </a>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
  name: "ChatSessionList",
});
</script>

<style scoped>
.session-list {
  height: 100%;
  overflow-y: auto;
}

.session-list .session-item {
  display: flex;
  align-items: center;
  height: 60px;
  cursor: pointer;
  margin-top: 1px;
  color: #aeaeae;
}

.session-item.is-active,
.session-item:hover {
  background: #2d3345;
}
</style>