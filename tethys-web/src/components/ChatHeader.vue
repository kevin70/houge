<template>
  <div class="header-container">
    <div class="form-container mr-3">
      <div class="row1">
        <div class="is-flex-grow-1 mr-3 mb-3">
          <input
            class="input c-input"
            placeholder="WebSocket URL"
            v-model="form.wsUrl"
            :readonly="connected.value"
          />
        </div>
        <div class="uid-div">
          <input
            class="input c-input"
            placeholder="用户 ID"
            v-model="form.uid"
            :readonly="connected.value"
          />
        </div>
      </div>
      <div>
        <input
          class="input c-input"
          placeholder="访问令牌"
          v-model="form.accessToken"
          :readonly="connected"
        />
      </div>
    </div>
    <div class="btn-div is-align-items-end">
      <button
        v-if="!connected.value"
        @click="connect"
        class="button is-size-5 btn is-primary"
      >
        连接
      </button>
      <button v-else @click="disconnect" class="button is-size-5 btn is-danger">
        断开
      </button>
    </div>
  </div>
</template>

<script>
import {} from "vue";

/**
 * 加载访问令牌.
 */
const loadAccessToken = (uid) => {
  return fetch(`http://127.0.0.1:11010/token/${uid}`, { method: "POST" })
    .then((resp) => resp.json())
    .then((json) => json.access_token);
};

export default {
  name: "ChatHeader",
  inject: ["connected", "updateConnected"],
  data: () => ({
    form: {
      wsUrl: "ws://127.0.0.1:11010/im",
      uid: "111",
      accessToken: null,
    },
    webSocket: null,
  }),
  methods: {
    connect() {
      new Promise((resolve, reject) => {
        const ac = this.form.accessToken;
        if (ac) {
          resolve(ac);
        } else {
          loadAccessToken(this.form.uid)
            .then((ac) => {
              this.form.accessToken = ac;
              return ac;
            })
            .then(resolve)
            .catch(reject);
        }
      })
        .then((ac) => {
          const socket = new WebSocket(`${this.form.wsUrl}?access_token=${ac}`);
          socket.addEventListener("open", this.socketOpen);
          socket.addEventListener("close", this.socketClose);
          socket.addEventListener("message", this.socketMessage);
          socket.addEventListener("error", this.socketError);
          this.webSocket = socket;
          return ac;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    disconnect() {
      this.updateConnected(false);
    },
    // < WebSocket callback
    socketOpen(event) {
      this.updateConnected(true);
    },
    socketClose(event) {
      console.log(`socket closed: ${this.webSocket}`);
      this.updateConnected(false);
    },
    socketMessage(event) {
      console.log(`=====message ${JSON.stringify(event)}`);
    },
    socketError(event) {
      console.error(`socket error: ${JSON.stringify(event)}`);
      this.updateConnected(false);
    },
    // WebSocket callback >
  },
};
</script>

<style scoped>
.header-container {
  display: flex;
  width: 100%;
}

.form-container {
  display: flex;
  width: 100%;
  flex-direction: column;
}

.row1 {
  display: flex;
  width: 100%;
  flex-direction: row;
  justify-content: space-between;
}

.uid-div {
  max-width: 300px;
}

.form-container .c-input {
  background: var(--black-ter);
  border: var(--black-ter);
  color: var(--grey-light);
}

.form-container .c-input::placeholder {
  color: var(--grey-light);
}

.form-container .c-input:read-only {
  color: var(--grey-dark);
}

.btn-div {
  min-width: 100px;
}

.btn-div .btn {
  width: 100%;
  height: 100%;
}
</style>