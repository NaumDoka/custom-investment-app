<script setup lang="ts">
import logoIcon from '@/assets/images/logos/logo-icon.png'
</script>

<template>
  <svg
      width="220"
      height="40"
      viewBox="0 0 220 40"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
  >
    <defs>
      <clipPath id="circle-view-mask">
        <circle cx="20" cy="20" r="20" />
      </clipPath>

      <linearGradient id="tnt-text-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" style="stop-color:#FCD34D; stop-opacity:1" />
        <stop offset="50%" style="stop-color:#49BEFF; stop-opacity:1" />
        <stop offset="100%" style="stop-color:#C8CEDC; stop-opacity:1" />
      </linearGradient>
    </defs>

    <image
        :href="logoIcon"
        x="0"
        y="0"
        width="40"
        height="40"
        clip-path="url(#circle-view-mask)"
    />

    <text
        x="50"
        y="27"
        fill="url(#tnt-text-gradient)"
        style="font-family: 'Inter', sans-serif; font-weight: 700; font-size: 20px; letter-spacing: -0.5px;"
    >
      TopNight<tspan style="font-weight: 300;">Trader</tspan>
    </text>
  </svg>
</template>