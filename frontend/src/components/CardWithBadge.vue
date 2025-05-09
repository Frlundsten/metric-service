<script setup>
const props = defineProps({
  img: String,
  title: String,
  description: String,
  extra: String,
  badges: {
    type: Array,
    required: true
  }
});
</script>

<template>
  <div class="card bg-base-100 shadow-sm">
    <figure>
      <img
          :src="img"
          alt="?"/>
    </figure>
    <div class="card-body">
      <h3 class="card-title">
        {{ title }}
      </h3>
      <p>{{ description }}
      </p>
      <div>
        <p class="text-center">Examples of such {{ extra }}</p>
        <div class="card-actions justify-center">
          <div
              v-for="(badge, index) in badges"
              :key="index"
              class="badge badge-outline"
          >
            {{ badge }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
