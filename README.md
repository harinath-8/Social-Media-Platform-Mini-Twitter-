# 🐦 Mini Twitter - Social Media Platform

## Overview
**Mini Twitter** is a lightweight social media platform designed with a **microservices architecture** using Spring Boot. The platform allows users to create posts (tweets), like and comment on posts, and follow/unfollow other users. Each service is independently scalable, ensuring robust performance and flexibility.

## ✨ Features

### User Management:
- 🔍 **View user profiles**: Fetch user profiles by ID.
- ✍️ **Register and log in**: Create a new user with email and password, secured with JWT-based authentication.

### Post Management:
- 📝 **Create new posts (tweets)**: Allow users to create posts with text content.
- 🔍 **View individual posts by ID**: Fetch the details of a specific post.
- 📰 **Fetch all posts by a specific user**: Retrieve all posts made by a specific user.
- 🗑️ **Delete posts created by the user**: Allow users to delete their own posts.

### Comment Management:
- 💬 **Add comments to posts**: Enable users to add comments to posts.
- 🔍 **Fetch comments for a specific post**: Retrieve all comments for a given post.
- 🗑️ **Delete comments from posts**: Allow users to delete their own comments.

### Like Management:
- ❤️ **Like/unlike posts**: Like or unlike a specific post.
- 💬 **Like/unlike comments**: Like or unlike a specific comment.
- 🔍 **Fetch the number of likes on a post**: Retrieve the total number of likes for a specific post.
- 🔍 **Fetch the number of likes on a comment**: Retrieve the total number of likes for a specific comment.

### Follow Management:
- 👥 **Follow/unfollow other users**: Manage follow/unfollow actions for users.
- 🔍 **View followers list**: Fetch the list of users who follow a specific user.
- 🔍 **View following list**: Fetch the list of users that a specific user is following.

---

## 🏗️ Microservices Architecture

Each feature is implemented as an independent microservice:
1. **User Service** - Handles user registration, login, and profile management.
2. **Post Service** - Manages tweet-related operations.
3. **Comment Service** - Handles post comments.
4. **Like Service** - Manages likes on posts.
5. **Follow Service** - Manages user follow/unfollow relationships.

Each service has its own database for data isolation and consistency, communicating via REST APIs.

---


## 📑 API Endpoints
Detailed information for each API endpoint:
1. [User Service](docs/user/user-service-api-spec.md)
2. [Post Service](docs/post/post-service-api-spec.md)
3. [Comment Service](docs/comment/comment-service-api-spec.md)
4. [Like Service](docs/like/like-service-api-spec.md)
5. [Follow Service](docs/follow/follow-service-api-spec.md)

## 🚀 Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Lilemanalu/mini-twitter.git
