<script setup>
import MaterialCalculator from './components/MaterialCalculator.vue'
</script>

<template>
  <div class="app-container">
    <MaterialCalculator />
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}
</style>
