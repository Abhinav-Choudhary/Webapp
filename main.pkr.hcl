packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = ">= 1.0.0, < 2.0.0"
    }
  }
}

variable "project_id" {
  type    = string
  default = "csye6225-abhinav-dev"
}

variable "region" {
  type    = string
  default = "us-east1"
}

variable "zone" {
  type    = string
  default = "us-east1-b"
}

variable "source_image_family" {
  type    = string
  default = "centos-stream-8" # centos stream image for CentOS Stream release 8
}

variable "disk_size" {
  type    = number
  default = 20
}

variable "disk_type" {
  type    = string
  default = "pd-standard"
}

variable "image_name" {
  type    = string
  default = "csye6225-{{timestamp}}"
}

variable "image_description" {
  type    = string
  default = "CSYE 6225 App Custom Image"
}

variable "image_family" {
  type    = string
  default = "csye6225-app-image"
}

variable "image_storage_locations" {
  type    = list(string)
  default = ["us"]
}

variable "ssh_username" {
  type    = string
  default = "packer"
}

variable "file_source" {
  type    = string
  default = "./target/cloud-0.0.1-SNAPSHOT.jar"
}

variable "file_target" {
  type    = string
  default = "/tmp/"
}

variable "service_file_source" {
  type    = string
  default = "./services/csye6225.service"
}

variable "service_file_target" {
  type    = string
  default = "/tmp/"
}

source "googlecompute" "csye6225-dev-app-custom-image" {
  project_id              = var.project_id
  source_image_family     = var.source_image_family
  region                  = var.region
  zone                    = var.zone
  disk_size               = var.disk_size
  disk_type               = var.disk_type
  image_name              = var.image_name
  image_description       = var.image_description
  image_family            = var.image_family
  image_project_id        = var.project_id
  image_storage_locations = var.image_storage_locations
  ssh_username            = var.ssh_username

}

build {
  sources = [
    "sources.googlecompute.csye6225-dev-app-custom-image",
  ]

  provisioner "file" {
    source      = var.file_source
    destination = var.file_target
  }

  provisioner "file" {
    source      = var.service_file_source
    destination = var.service_file_target
  }

  provisioner "shell" {
    scripts = [
      "./scripts/noLoginUser.sh",
      "./scripts/centOsSetup.sh",
      "./scripts/updateApplicationOwnership.sh",
      "./scripts/enableServices.sh"
    ]
  }
}