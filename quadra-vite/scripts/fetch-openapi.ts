import axios from 'axios';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 微服务端口配置
const services = [
  { name: 'content', port: 18081 },
  { name: 'interaction', port: 18082 },
  { name: 'recommend', port: 18083 },
  { name: 'social', port: 18084 },
  { name: 'system', port: 18085 },
  { name: 'user', port: 18086 },
];

const OUTPUT_DIR = path.resolve(__dirname, '../openapi');

async function fetchOpenApi(service: { name: string; port: number }) {
  const url = `http://localhost:${service.port}/v3/api-docs`;
  
  try {
    console.log(`📡 Fetching OpenAPI from ${service.name} (${url})...`);
    const response = await axios.get(url, { timeout: 5000 });
    
    if (response.data) {
      const outputPath = path.join(OUTPUT_DIR, `${service.name}-api.json`);
      fs.writeFileSync(outputPath, JSON.stringify(response.data, null, 2));
      console.log(`✅ ${service.name} API saved to ${outputPath}`);
      return true;
    }
  } catch (error: any) {
    if (error.code === 'ECONNREFUSED') {
      console.warn(`⚠️  ${service.name} service is not running on port ${service.port}`);
    } else {
      console.error(`❌ Failed to fetch ${service.name}:`, error.message);
    }
    return false;
  }
}

async function main() {
  console.log('🚀 Starting to fetch OpenAPI documents...\n');
  
  // 创建输出目录
  if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
  }
  
  const results: Record<string, boolean> = {};
  
  for (const service of services) {
    results[service.name] = await fetchOpenApi(service);
  }
  
  console.log('\n📊 Summary:');
  console.log('─'.repeat(50));
  Object.entries(results).forEach(([name, success]) => {
    const icon = success ? '✅' : '❌';
    console.log(`${icon} ${name}: ${success ? 'Success' : 'Failed'}`);
  });
  
  const successCount = Object.values(results).filter(Boolean).length;
  console.log('─'.repeat(50));
  console.log(`\n🎉 Completed: ${successCount}/${services.length} services`);
}

main().catch(console.error);
