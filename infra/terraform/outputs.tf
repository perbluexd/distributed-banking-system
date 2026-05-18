output "vpc_id" {
  description = "Main VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = aws_subnet.public[*].id
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.main.name
}

output "ecs_security_group_id" {
  description = "ECS security group ID"
  value       = aws_security_group.ecs.id
}

output "ecr_repository_urls" {
  description = "ECR repository URLs for all microservices"
  value = {
    for service, repo in aws_ecr_repository.microservices :
    service => repo.repository_url
  }
}