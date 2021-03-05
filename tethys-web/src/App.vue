<template>
  <div class="chat-container">
    <div class="chat-inner">
      <div class="chat-top px-4">
        <chat-header></chat-header>
      </div>
      <div class="chat-left">
        <div class="brand py-3">
          <a href="https://gitee.com/kk70/tethys" target="_blank">
            <div class="is-size-3 has-text-link-light">Tethys IM</div></a
          >
        </div>
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

<script>
import { defineComponent } from "vue";
import ChatAction from "./components/ChatAction.vue";
import ChatHeader from "./components/ChatHeader.vue";
import ChatMessageList from "./components/ChatMessageList.vue";
import ChatSessionList from "./components/ChatSessionList.vue";

export default defineComponent({
  name: "App",
  components: { ChatAction, ChatSessionList, ChatMessageList, ChatHeader },
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
}

.brand {
  display: flex;
  height: var(--chat-top-height);
  justify-content: center;
  align-items: center;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--black);
  border-bottom: 1px solid var(--grey-dark);
}

.chat-container .chat-left {
  position: absolute;
  top: 0;
  left: 0;
  width: var(--chat-left-width);
  height: 100%;
  background: var(--black);
  border-right: 1px solid var(--grey-dark);
}

.chat-container .chat-right {
  position: absolute;
  top: var(--chat-top-height);
  left: var(--chat-left-width);
  height: calc(100% - var(--chat-top-height));
  width: calc(100% - var(--chat-left-width));
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
  z-index: 1;
}
</style>