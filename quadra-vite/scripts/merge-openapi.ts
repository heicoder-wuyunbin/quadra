import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 输入输出目录配置
const INPUT_DIR = path.resolve(__dirname, '../../openapi');
const OUTPUT_DIR = path.resolve(__dirname, '../openapi');

// 微服务文件映射
const serviceFiles = [
  { name: 'content', file: 'content.json' },
  { name: 'interaction', file: 'interaction.json' },
  { name: 'recommend', file: 'recommonend.json' },
  { name: 'social', file: 'social.json' },
  { name: 'system', file: 'system.json' },
  { name: 'user', file: 'user.json' },
];

async function mergeOpenApi() {
  console.log('🚀 Starting to merge OpenAPI documents...\n');
  
  // 创建输出目录
  if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
  }
  
  const apis: any[] = [];
  
  for (const service of serviceFiles) {
    const inputPath = path.join(INPUT_DIR, service.file);
    
    try {
      if (!fs.existsSync(inputPath)) {
        console.warn(`⚠️  ${service.name} API file not found: ${inputPath}`);
        continue;
      }
      
      console.log(`📡 Reading ${service.name} API from ${inputPath}...`);
      const content = fs.readFileSync(inputPath, 'utf-8');
      const api = JSON.parse(content);
      
      // 保存到输出目录
      const outputPath = path.join(OUTPUT_DIR, `${service.name}-api.json`);
      fs.writeFileSync(outputPath, JSON.stringify(api, null, 2));
      console.log(`✅ ${service.name} API copied to ${outputPath}`);
      
      apis.push(api);
    } catch (error: any) {
      console.error(`❌ Failed to process ${service.name}:`, error.message);
    }
  }
  
  if (apis.length > 0) {
    // 合并所有 API 到第一个 API 中
    const combinedApi = apis[0];
    
    // 合并 paths
    for (let i = 1; i < apis.length; i++) {
      Object.assign(combinedApi.paths, apis[i].paths);
      Object.assign(combinedApi.components.schemas, apis[i].components?.schemas || {});
    }
    
    // 保存合并后的 API
    const combinedPath = path.join(OUTPUT_DIR, 'combined-api.json');
    fs.writeFileSync(combinedPath, JSON.stringify(combinedApi, null, 2));
    console.log(`\n✅ Combined API saved to ${combinedPath}`);
    console.log(`📊 Total paths: ${Object.keys(combinedApi.paths).length}`);
  }
  
  console.log('\n🎉 Completed!');
}

mergeOpenApi().catch(console.error);
