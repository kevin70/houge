<script setup>
import { defineProps, reactive } from "vue";
import ChatHeader from "./_ChatHeader.vue";

const name = "kk70";
</script>

<template>
  <div class="min-h-screen flex flex-col mt-12">
    <div class="md:container md:mx-auto">
      <chat-header />
      <div class="flex flex-row pt-2 gap-x-2">
        <div class="w-64 border">
          <!-- 当前用户的信息 -->
          <div class="flex flex-row">
            <div
              class="rounded-full h-12 w-12 flex items-center justify-center bg-green-500 cursor-pointer"
            >
              <span class="m-0.5 truncate">A 7894564</span>
            </div>
            <!-- 添加私人或群组会话 -->
            <div
              class="rounded-full h-10 w-10 flex items-center justify-center bg-green-500 cursor-pointer"
            >
              <i class="text-4xl iconfont icon-plus"></i>
            </div>
          </div>
          <!-- 会话列表 -->
          <div></div>
        </div>

        <div class="flex-grow border">Right</div>
      </div>
    </div>
  </div>
</template>
