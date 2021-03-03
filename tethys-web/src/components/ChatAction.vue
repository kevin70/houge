<template>
  <div class="control">
    <textarea
      placeholder="在此对话中发送消息"
      class="textarea p-2"
      rows="2"
    ></textarea>
    <div class="toolbar">
      <button class="button is-primary" style="height: 100%">发 送</button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
  name: "ChatAction",
});
</script>

<style scoped>
.control {
  box-sizing: border-box;
  clear: both;
  font-size: 1rem;
  position: relative;
  text-align: inherit;
}

.control textarea {
  background: #191c26;
  border-color: #191c26;
  color: #aeaeae;
}

textarea::placeholder {
  color: #aeaeae;
}

.textarea {
  resize: none;
  height: 100%;
  line-height: 2;
  box-shadow: none !important;
  transition: 0.3s;
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