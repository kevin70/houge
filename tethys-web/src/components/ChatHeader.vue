<template>
  <div class="header-container">
    <div class="form-container mr-3">
      <div class="row1">
        <div class="is-flex-grow-1 mr-3 mb-3">
          <input
            class="input c-input"
            placeholder="WebSocket URL"
            :readonly="connected"
          />
        </div>
        <div class="uid-div">
          <input
            class="input c-input"
            placeholder="用户 ID"
            :readonly="connected"
          />
        </div>
      </div>
      <div>
        <input
          class="input c-input"
          placeholder="访问令牌"
          :readonly="connected"
        />
      </div>
    </div>
    <div class="btn-div is-align-items-end">
      <button
        v-if="!connected"
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

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
  name: "ChatHeader",
  data: () => ({
    connected: false,
  }),
  methods: {
    connect() {
      this.connected = true;
    },
    disconnect() {
      this.connected = false;
    },
  },
});
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