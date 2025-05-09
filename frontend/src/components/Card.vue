<script setup>
const props = defineProps(['img', 'title', 'description'])
</script>

<template>
  <div class="hero bg-base-200 min-h-screen">
    <div class="hero-content text-center flex flex-col">


      <div class="max-w-lg chat chat-start">
        <p class="w-80 italic chat-bubble chat-bubble-accent">
          Our customer called, they are unable to reach their service. The website is down and they're losing business.
          It seems like our server isn't handling the traffic properly...
        </p>
      </div>

      <h3 class="w-1/2">
        All those unit tests, integration tests, and end-to-end tests you spent hours crafting. The ones that gave you
        confidence, covered edge cases, and passed with flying colors, are now useless. They
        didn’t catch this.
        <br><br>
        What could have helped? <strong class="text-2xl">A performance test</strong>. It wouldn’t just have caught the
        issue — it would probably have warned
        you that this failure was even possible.
      </h3>
      <button @click="$emit('to-info')" class="btn btn-primary">What is a performance test?</button>

    </div>
  </div>
</template>