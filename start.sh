#!/bin/bash

echo "========================================="
echo "  高校社团管理系统 启动脚本"
echo "========================================="

# 启动后端
echo ""
echo "[1/2] 正在启动后端服务..."
cd "$(dirname "$0")/club-backend"
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo ""
echo "等待后端启动完成..."
sleep 8

# 启动前端
echo ""
echo "[2/2] 正在启动前端服务..."
cd club-management
npm run dev &
FRONTEND_PID=$!
cd ..

echo ""
echo "========================================="
echo "  启动完成！"
echo "  前端: http://localhost:3000"
echo "  后端: http://localhost:8080"
echo "========================================="
echo ""
echo "按 Ctrl+C 停止所有服务"

# 捕获退出信号
trap 'kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit' INT TERM EXIT

# 等待进程
wait
