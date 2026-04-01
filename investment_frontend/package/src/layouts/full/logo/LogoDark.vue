<script setup lang="ts">

</script>
<template>
  <svg width="220" height="40" viewBox="0 0 220 40" fill="none" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <clipPath id="circleView">
        <circle cx="20" cy="20" r="20" />
      </clipPath>
    </defs>
    <image
        href="@/assets/images/logos/logo-icon.png"
        x="0" y="0"
        width="40" height="40"
        clip-path="url(#circleView)"
    />

    <text
        x="50"
        y="27"
        fill="black"
        style="font-family: 'Inter', sans-serif; font-weight: 700; font-size: 20px; letter-spacing: -0.5px;"
    >
      TopNight<tspan style="font-weight: 300;">Trader</tspan>
    </text>
  </svg>
</template>