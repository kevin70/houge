<template>
  <div class="control">
    <textarea
      placeholder="在此对话中发送消息"
      class="textarea p-2 send-msg-input"
      v-model="message"
      rows="2"
    ></textarea>
    <div class="toolbar">
      <button
        class="button is-primary"
        style="height: 100%"
        :disabled="!connected.value || !selectedSessionId.value || !message"
        @click="send"
      >
        发 送
      </button>
    </div>
  </div>
</template>

<script>
import {} from "vue";
export default {
  name: "ChatAction",
  inject: [
    "connected",
    "sendMessageConsumers",
    "currentLoginUid",
    "selectedSessionId",
  ],
  data: () => ({
    message: null,
  }),
  methods: {
    send() {
      if (!this.message) {
        return;
      }

      const sendMessageConsumers = this.sendMessageConsumers.value;
      if (sendMessageConsumers.length == 0) {
        console.warn("没有注册发送消息消费者");
      }

      const m = {
        "@ns": "p.msg",
        from: this.currentLoginUid.value,
        to: this.selectedSessionId.value,
        content: this.message,
        content_kind: 1,
      };
      sendMessageConsumers.forEach((handle) => {
        handle(m);
      });

      this.message = null;
    },
  },
};
</script>

<style scoped>
.control {
  position: relative;
  box-sizing: border-box;
  clear: both;
  font-size: 1rem;
  text-align: inherit;
}

.send-msg-input {
  resize: none;
  height: 100%;
  line-height: 2;
  background: var(--black-ter);
  border: var(--black-ter);
  color: var(--grey-light);
}

.control .send-msg-input::placeholder {
  color: var(--grey-light);
}

.toolbar {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>