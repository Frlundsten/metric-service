<script setup>
import { ref } from 'vue';
import Card from "@/components/Card.vue";
import HorizontalCard from "@/components/HorizontalCard.vue";
import Info from "@/components/Info.vue";

const infoSection = ref(null);

function scrollToInfo() {
  infoSection.value?.scrollIntoView({ behavior: 'smooth' });
}
</script>

<template>
  <div class="w-full h-full flex items-center flex-col">
    <div class="w-full h-2/3">
      <Card @to-info="scrollToInfo"/>
    </div>
    <div ref="infoSection">
      <Info />
    </div>
    <div class="max-w-4xl ">
      <ul class="steps w-full steps-vertical pb-12">
        <li class="step step-primary">
          <span class="step-icon material-symbols-outlined">settings</span>
          <HorizontalCard class="mx-auto w-4/5" img="requests.png" title="Run your test" description="Run your K6 script to simulate a surge of concurrent requests to test the performance of your server."
          />
        </li>
        <li class="step step-secondary">
          <span class="step-icon  material-symbols-outlined">database</span>
          <HorizontalCard class="mx-auto w-4/5"  img="db.png" title="Persist the data" description="Store the summary from the K6 test in a database or any storage solution of your choice."
          />
        </li>
        <li class="step step-success">
          <span class="step-icon material-symbols-outlined">monitoring</span>
          <HorizontalCard class="mx-auto w-4/5" img="data.png" title="Fetch the data" description="Retrieve stored metrics and analyze them. Visualize the data with graphs and charts using tools like Grafana, or build your own frontend for custom display."/>
        </li>
      </ul>
    </div>
  </div>
</template>
