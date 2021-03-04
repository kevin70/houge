<template>
  <div class="inner-container">
    <div class="session-list">
      <a class="session-item pl-2 is-active">
        <figure class="image is-48x48">
          <img
            class="is-rounded"
            src="https://via.placeholder.com/100?text=KK"
          />
        </figure>
        <div class="ml-2">我是KK</div>
      </a>
      <a class="session-item pl-2">
        <figure class="image is-48x48">
          <img
            class="is-rounded"
            src="https://via.placeholder.com/100?text=KK"
          />
        </figure>
        <div class="ml-2">我是KK</div>
      </a>
    </div>
    <div class="plus-container">
      <button class="button plus-btn">
        <i class="iconfont icon-plus has-text-white is-size-5"></i>
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
  name: "ChatSessionList",
});
</script>

<style scoped>
:inner-var {
  --plus-container-height: 84px;
}

.inner-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: calc(100% - var(--chat-top-height));
}

.inner-container .plus-container {
  display: flex;
  justify-content: center;
  width: 100%;
  height: var(--plus-container-height);
  padding-bottom: 20px;
}

.session-list {
  width: 100%;
  height: calc(100% - var(--chat-top-height) - var(--plus-container-height));
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
  background: var(--grey);
}

.plus-btn {
  height: 48px;
  width: 48px;
  border: 3px dotted var(--grey-light);
  border-radius: 50%;
  background: var(--grey-lighter);
}
</style>