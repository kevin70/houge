<template>
  <button class="button is-primary">Dark</button>
  <br />
  <span class="icon-text">
    <span class="icon">
      <i class="iconfont icon-plus"></i>
    </span>
    <span>Home</span>
  </span>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "App",
});
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>