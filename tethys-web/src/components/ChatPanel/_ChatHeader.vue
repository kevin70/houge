<script setup>
import { reactive } from "vue";
const state = reactive({
  connected: false,
});

/**
 * 连接 WebSocket.
 */
const connect = () => {
  state.connected = true;
};

const disconnect = () => {
  state.connected = false;
};
</script>
<template>
  <div class="flex gap-x-2">
    <div class="flex-grow flex flex-col gap-2">
      <div class="flex flex-row gap-x-2">
        <div class="flex-grow">
          <input type="text" placeholder="WebSocket URL" class="md:w-full" />
        </div>
        <div class="w-24">
          <input type="text" placeholder="UID" class="md:w-full" />
        </div>
      </div>
      <div>
        <input type="text" placeholder="access_token" class="md:w-full" />
      </div>
    </div>
    <div class="w-24 flex content-center">
      <button
        v-if="!state.connected"
        class="md:w-full dark:text-white hover:text-red-500"
        type="button"
        @click="connect"
      >
        连接
      </button>
      <button
        v-else
        class="md:w-full dark:text-white hover:text-red-500"
        type="button"
        @click="disconnect"
      >
        断开链接
      </button>
    </div>
  </div>
</template>