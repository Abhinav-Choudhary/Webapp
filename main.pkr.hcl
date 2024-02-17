packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = ">= 1.0.0"
    }
  }
}

variable "project" {
    type = string
    default = "northeastern-cloud"
}

variable "region" {
    type = string
    default = "us-east1"
}

variable "zone" {
    type  = string
    default = "us-east1-b"
}

variable "source_image" {
    type = string
    default = "centos-stream-8" # centos stream image for CentOS Stream release 8
}

variable "disk_size" {
    type = number
    default = 20
}

variable "disk_type" {
    type = string
    default = "pd-standard"
}


source "googlecompute" "csye6225-app-custom-image" {
    project_id = var.project
    source_image_family = var.source_image
    region = var.region
    zone = var.zone
    disk_size = var.disk_size
    disk_type = var.disk_type
    image_name = "csye6225-{{timestamp}}"
    image_description = "CSYE 6225 App Custom Image"
    image_family = "csye6225-app-image"
    image_project_id = var.project
    image_storage_locations = ["us"]
    ssh_username = "packer"

}

build {
    sources = [
        "sources.googlecompute.csye6225-app-custom-image",
    ]

    provisioner "file" {
        source = "./target/cloud-0.0.1-SNAPSHOT.jar"

        destination = "/tmp/"
    }

    provisioner "shell" {
        scripts = ["./scripts/centOsSetup.sh"]
    }
}