<template>
  <div class="inner-container">
    <div class="session-list">
      <a
        class="session-item pl-2"
        v-for="session in sessions"
        :key="session.id"
        @click="updateSelectedSessionId(session.id)"
        :class="selectedSessionId.value == session.id ? 'is-active' : null"
      >
        <figure class="image is-48x48">
          <img class="is-rounded" src="https://via.placeholder.com/100" />
        </figure>
        <div class="ml-2">{{ session.id }}</div>
      </a>
    </div>
    <div class="plus-container">
      <button class="button plus-btn" @click="addSessionModal(true)">
        <i class="iconfont icon-plus has-text-white is-size-5"></i>
      </button>
    </div>

    <!-- 添加会话窗口 -->
    <div class="modal" :class="addSessionActive ? 'is-active' : null">
      <div class="modal-background" @click="addSessionModal(false)"></div>
      <div class="modal-content">
        <input class="input" placeholder="输入用户 ID" v-model="addSessionId" />
        <button class="button is-info" @click="addSession()">添加</button>
      </div>
    </div>
  </div>
</template>

<script>
const sessions = [
  {
    id: "111",
  },
  {
    id: "222",
  },
  {
    id: "333",
  },
];

export default {
  name: "ChatSessionList",
  inject: ["selectedSessionId", "updateSelectedSessionId"],
  data: () => ({
    sessions: sessions,
    addSessionActive: false,
    addSessionId: null,
  }),
  methods: {
    addSessionModal(v) {
      this.addSessionActive = v;
    },
    addSession() {
      const addSessionId = this.addSessionId;
      if (!addSessionId) {
        return;
      }
      for (const s in this.sessions) {
        if (s.id === addSessionId) {
          return;
        }
      }
      this.sessions = this.sessions.concat({ id: addSessionId });
      this.addSessionModal(false);
    },
  },
};
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