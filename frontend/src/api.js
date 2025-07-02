/**
 * 计算物料需求
 * @param {Array<{productId: string, quantity: number}>} items
 * @returns {Promise<Array<{materialId: string, materialName: string, quantity: number}>>}
 */
export async function calculateMaterials(items) {
  const response = await fetch('/api/subcontracting/calculate-materials', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ items }),
  });
  if (!response.ok) {
    throw new Error('请求失败');
  }
  return await response.json();
}

/**
 * 获取所有可用产品（成品）
 * @returns {Promise<Array<{id: string, name: string}>>}
 */
export async function fetchProducts() {
  const response = await fetch('/api/subcontracting/products');
  if (!response.ok) {
    throw new Error('获取产品列表失败');
  }
  return await response.json();
} 