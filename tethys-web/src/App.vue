<template>
  <div class="chat-container">
    <div class="chat-inner">
      <div class="chat-top px-4">这里是头部信息</div>
      <div class="chat-left">
        <chat-session-list></chat-session-list>
      </div>
      <div class="chat-right">
        <div class="chat-msg-body">
          <chat-message-list></chat-message-list>
        </div>
        <div class="chat-action px-6">
          <chat-action></chat-action>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import ChatAction from "./components/ChatAction.vue";
import ChatMessageList from "./components/ChatMessageList.vue";
import ChatSessionList from "./components/ChatSessionList.vue";

export default defineComponent({
  name: "App",
  components: { ChatAction, ChatSessionList, ChatMessageList },
});
</script>

<style>
@import "./variables.css";

.chat-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  max-height: 100vh;
  overflow: hidden;
  transition: 0.3s;
}

.chat-container .chat-inner {
  position: relative;
  width: 100%;
  height: 100%;
}

.chat-container .chat-top {
  position: absolute;
  top: 0;
  left: var(--chat-left-width);
  width: calc(100% - var(--chat-left-width));
  height: var(--chat-top-height);
  background: #191c26;
  border-bottom: 1px solid #191c26;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 9;
  transition: 0.3s;
}

.chat-container .chat-left {
  position: absolute;
  top: 0;
  left: 0;
  width: var(--chat-left-width);
  height: 100%;
  z-index: 10;
  background: #191c26;
  transition: 0.3s;
}

.chat-container .chat-right {
  position: absolute;
  top: var(--chat-top-height);
  left: var(--chat-left-width);
  height: calc(100% - var(--chat-top-height));
  width: calc(100% - var(--chat-left-width));
  background: #212532;
  transition: 0.3s;
}

.chat-container .chat-msg-body {
  position: relative;
  width: 100%;
  height: calc(100% - var(--chat-top-height) - var(--chat-action-height));
  overflow-y: auto;
  animation-name: fadeInLeft;
  animation-duration: 0.5s;
}

.chat-container .chat-action {
  position: fixed;
  bottom: 8px;
  left: var(--chat-left-width);
  width: calc(100% - var(--chat-left-width));
  height: var(--chat-action-height);
  background: #212532;
  z-index: 1;
  transition: 0.3s;
}
</style>