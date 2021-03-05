<template>
  <div class="chat-container">
    <div class="error-message" v-if="errorMessage.shown">
      <div class="notification is-danger is-light">
        <button class="delete" @click="closeErrorMessage()"></button>
        <div v-html="errorMessage.currentMessage"></div>
      </div>
    </div>

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
import { provide, readonly, ref } from "vue";
import ChatAction from "./components/ChatAction.vue";
import ChatHeader from "./components/ChatHeader.vue";
import ChatMessageList from "./components/ChatMessageList.vue";
import ChatSessionList from "./components/ChatSessionList.vue";

export default {
  name: "App",
  components: { ChatAction, ChatSessionList, ChatMessageList, ChatHeader },
  data: () => ({
    errorMessage: {
      shown: false,
      currentMessage: "",
      messages: ["123456", "64789"],
    },
  }),
  setup() {
    // WebSocket 连接状态
    const connected = ref(false);
    const updateConnected = (v) => {
      connected.value = v;
    };
    provide("connected", readonly(connected));
    provide("updateConnected", updateConnected);

    // WebSocket 发送的消息的消费者
    const sendMessageConsumers = ref([]);
    const registerSendMessageConsumer = (v) => {
      sendMessageConsumers.value = sendMessageConsumers.value.concat(v);
    };
    provide("sendMessageConsumers", readonly(sendMessageConsumers));
    provide("registerSendMessageConsumer", registerSendMessageConsumer);

    // WebSocket 接收到的消息的消费者
    const receiveMessageConsumers = ref([]);
    const registerReceiveMessageConsumer = (v) => {
      receiveMessageConsumers.value = receiveMessageConsumers.value.concat(v);
    };
    provide("receiveMessageConsumers", receiveMessageConsumers);
    provide("registerReceiveMessageConsumer", registerReceiveMessageConsumer);

    // 当前登录的用户 ID
    const currentLoginUid = ref(null);
    const updateCurrentLoginUid = (v) => {
      currentLoginUid.value = v;
    };
    provide("currentLoginUid", readonly(currentLoginUid));
    provide("updateCurrentLoginUid", updateCurrentLoginUid);

    // 当前选中的会话
    const selectedSessionId = ref(null);
    const updateSelectedSessionId = (v) => {
      selectedSessionId.value = v;
    };
    provide("selectedSessionId", readonly(selectedSessionId));
    provide("updateSelectedSessionId", updateSelectedSessionId);
  },
  methods: {
    closeErrorMessage() {
      this.errorMessage.shown = false;
    },
    showErrorMessage() {
      const messages = this.errorMessage.messages;
      if (!messages || messages.length == 0) {
        this.errorMessage.shown = false;
        return;
      }
      const m = messages[0];
      this.errorMessage.messages = messages.slice(1);
      this.errorMessage.currentMessage = m;
      this.errorMessage.shown = true;
      setTimeout(() => {
        this.showErrorMessage();
      }, 2000);
    },
  },
};
</script>

<style>
@import "./variables.css";

.error-message {
  position: absolute;
  top: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 99999;
  width: 400px;
}

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