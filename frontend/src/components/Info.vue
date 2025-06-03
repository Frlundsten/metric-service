<script setup>
import CardWithBadge from "@/components/CardWithBadge.vue";
const tests = ['Stress', 'Load', 'Soak'];
const tools = ['Grafana K6', 'Gatling', 'jmeter'];

</script>

<template>
  <div class="flex justify-center mt-3">
    <div class="carousel md:w-1/2 w-2/3">
      <div id="slide1" class="carousel-item relative w-full">
        <CardWithBadge img="/cloud.png" title="What is a performance test?" description="Performance testing is essential for ensuring that servers can handle peak traffic and heavy loads.
        Many tools allow developers to simulate various conditions and analyze how the system behaves under stress, such as high traffic or prolonged usage."
                       extra="tests are" :badges="tests"/>
        <div class="absolute left-5 right-5 top-1/2 lg:flex -translate-y-1/2 transform justify-between hidden">
          <a href="#slide2" class="btn btn-circle">❮</a>
          <a href="#slide2" class="btn btn-circle">❯</a>
        </div>
      </div>
      <div id="slide2" class="carousel-item relative w-full">
        <CardWithBadge  img="/cloud.png" title="Tools"
                       description="There are various performance testing tools, each with its unique features and strengths."
                       extra="tools are" :badges="tools"/>
        <div class="absolute left-5 right-5 top-1/2 lg:flex -translate-y-1/2 transform justify-between hidden">
          <a href="#slide1" class="btn btn-circle">❮</a>
          <a href="#slide1" class="btn btn-circle">❯</a>
        </div>
      </div>
    </div>
  </div>
</template>