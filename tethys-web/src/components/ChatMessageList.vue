<template>
  <div ref="messagePanel" class="message-panel py-5 px-5">
    <div
      v-for="message in messages"
      :key="message"
      class="message-item"
      :class="
        currentLoginUid.value === message.from ? 'is-sent' : 'is-received'
      "
    >
      <figure class="image is-48x48">
        <img
          class="is-rounded"
          src="https://via.placeholder.com/100"
          :alt="message.from"
        />
      </figure>
      <div class="message-block">
        <span>2021-03-03T17:12:33</span>
        <div class="message-text p-2">
          {{ message.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { watch } from "vue";
export default {
  name: "ChatMessageList",
  inject: [
    "currentLoginUid",
    "selectedSessionId",
    "registerSendMessageConsumer",
    "registerReceiveMessageConsumer",
  ],
  data: () => ({
    messages: [],
  }),
  mounted() {
    // ======================
    this.registerSendMessageConsumer((message) => {
      this.messages = this.messages.concat(message);
    });
    this.registerReceiveMessageConsumer((message) => {
      console.log(`ChatMessageList 收到消息：${JSON.stringify(message)}`);
      this.messages = this.messages.concat(message);
    });

    //
    watch(this.messages, () => {
      this.$nextTick(() => {
        const e = this.$refs.messagePanel;
        e.scrollTop = e.offsetHeight;
      });
    });
  },
};
</script>

<style scoped>
.message-panel {
  position: relative;
  width: 100%;
  height: 100%;
  overflow-y: auto;
  animation-name: fadeInLeft;
  animation-duration: 0.5s;
}

.message-panel::-webkit-scrollbar {
  width: 8px !important;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-top: 5px;
}

.message-item.is-sent {
  flex-direction: row-reverse;
}

.message-item.is-received figure {
  margin-right: 15px;
}

.message-item.is-sent figure {
  margin-left: 15px;
}

.message-item .message-block {
  position: relative;
  max-width: 320px;
}

.message-block span {
  display: block;
  width: 100%;
  font-size: 0.8rem;
  color: #999;
}

.message-item.is-received .message-block span {
  text-align: right;
}

.message-item .message-text {
  background: var(--black-ter);
  color: var(--grey-light);
}
</style>