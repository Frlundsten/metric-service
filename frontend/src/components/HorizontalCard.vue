<script setup>
const props = defineProps(['img', 'title', 'description'])
</script>
<template>
  <div class="mt-10 shadow-[0px_0px_20px_0px_rgba(0,0,0,0.15)] justify-between p-10 rounded-2xl max-w-sm  lg:max-w-full lg:flex">
    <div class="basis-1/2">
      <img class="object-contain lg:h-[300px] w-full rounded-4xl" :src="props.img" alt="">
    </div>
    <div
        class="basis-1/2 bg-white rounded-b lg:rounded-b-none lg:rounded-r p-4 flex flex-col justify-between leading-normal">
      <div>
        <div class="text-gray-900 font-bold text-xl mb-2"><h4>{{ title }}</h4></div>
        <p class="text-gray-700 text-base">{{
            description
          }}</p>
      </div>
    </div>
  </div>
</template>