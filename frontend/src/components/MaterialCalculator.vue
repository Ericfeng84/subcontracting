<template>
  <div class="sap-page">
    <div class="sap-header-card">
      <div class="sap-header-title">物料需求计算</div>
      <div class="sap-header-desc">根据产品订单，智能计算所需原材料</div>
    </div>
    <div class="sap-card sap-input-card">
      <form @submit.prevent="handleSubmit">
        <div v-for="(item, idx) in items" :key="idx" class="sap-form-row">
          <select v-model="item.productId" required class="sap-input-select">
            <option value="" disabled>请选择产品</option>
            <option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }} ({{ p.id }})</option>
          </select>
          <input v-model.number="item.quantity" type="number" min="1" placeholder="数量" required class="sap-input-number" />
          <button type="button" class="sap-btn sap-btn-secondary" @click="removeItem(idx)" v-if="items.length > 1">删除</button>
        </div>
        <div class="sap-form-actions">
          <button type="button" class="sap-btn sap-btn-secondary" @click="addItem">添加产品</button>
          <button type="submit" class="sap-btn sap-btn-primary">计算物料</button>
        </div>
      </form>
      <div v-if="error" class="sap-error">{{ error }}</div>
    </div>
    <div v-if="result.length" class="sap-card sap-result-card">
      <div class="sap-section-title">所需原材料</div>
      <table class="sap-table">
        <thead>
          <tr>
            <th>原材料ID</th>
            <th>原材料名称</th>
            <th>数量</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="mat in result" :key="mat.materialId">
            <td>{{ mat.materialId }}</td>
            <td>{{ mat.materialName }}</td>
            <td>{{ mat.totalQuantityRequired }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { calculateMaterials, fetchProducts } from '../api';

const items = ref([
  { productId: '', quantity: 1 }
]);
const result = ref([]);
const error = ref('');
const products = ref([]);

onMounted(async () => {
  try {
    products.value = await fetchProducts();
  } catch (e) {
    error.value = e.message || '产品列表加载失败';
  }
});

function addItem() {
  items.value.push({ productId: '', quantity: 1 });
}
function removeItem(idx) {
  items.value.splice(idx, 1);
}
async function handleSubmit() {
  error.value = '';
  result.value = [];
  if (items.value.some(item => !item.productId || item.quantity <= 0)) {
    error.value = '请选择产品并输入大于0的数量';
    return;
  }
  try {
    result.value = await calculateMaterials(items.value);
  } catch (e) {
    error.value = e.message || '请求失败';
  }
}
</script>

<style scoped>
.sap-page {
  background: #f4f6f8;
  min-height: 100vh;
  padding: 32px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.sap-header-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 32px 48px 24px 48px;
  margin-bottom: 32px;
  width: 600px;
  max-width: 90vw;
  text-align: left;
}
.sap-header-title {
  font-size: 2rem;
  font-weight: 600;
  color: #22334d;
  margin-bottom: 8px;
}
.sap-header-desc {
  color: #6a7a90;
  font-size: 1.1rem;
}
.sap-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 32px 40px 24px 40px;
  margin-bottom: 32px;
  width: 600px;
  max-width: 90vw;
}
.sap-input-card {
  margin-bottom: 24px;
}
.sap-form-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.sap-input-select, .sap-input-number {
  padding: 8px 12px;
  border: 1px solid #cfd8dc;
  border-radius: 6px;
  font-size: 1rem;
  margin-right: 12px;
  background: #f9fafb;
  min-width: 200px;
}
.sap-input-number {
  width: 90px;
}
.sap-btn {
  padding: 7px 18px;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  margin-right: 10px;
  transition: background 0.2s;
}
.sap-btn-primary {
  background: #0a6ed1;
  color: #fff;
}
.sap-btn-primary:hover {
  background: #0854a0;
}
.sap-btn-secondary {
  background: #e3eaf2;
  color: #22334d;
}
.sap-btn-secondary:hover {
  background: #cfd8dc;
}
.sap-form-actions {
  margin-top: 8px;
}
.sap-error {
  color: #d32f2f;
  margin-top: 12px;
  font-size: 1rem;
}
.sap-section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #22334d;
  margin-bottom: 16px;
}
.sap-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  font-size: 1rem;
}
.sap-table th, .sap-table td {
  border: 1px solid #e3eaf2;
  padding: 10px 14px;
  text-align: left;
}
.sap-table th {
  background: #f4f6f8;
  color: #22334d;
  font-weight: 600;
}
</style>