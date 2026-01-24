const API_BASE_URL = 'http://localhost:8080/api';
let currentUser = null;
let currentPage = 0;
let currentBlogId = null;

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadBlogs();
    loadCategories();
    setupEventListeners();
});

// Check if user is authenticated
function checkAuth() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    if (token && user) {
        currentUser = JSON.parse(user);
        showUserMenu();
    }
}

// Show user menu
function showUserMenu() {
    document.getElementById('auth-buttons').style.display = 'none';
    document.getElementById('user-menu').style.display = 'flex';
    document.getElementById('username-display').textContent = currentUser.username;
}

// Hide user menu
function hideUserMenu() {
    document.getElementById('auth-buttons').style.display = 'flex';
    document.getElementById('user-menu').style.display = 'none';
}

// Setup event listeners
function setupEventListeners() {
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('register-form').addEventListener('submit', handleRegister);
    document.getElementById('create-blog-form').addEventListener('submit', handleCreateBlog);
    document.getElementById('search-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') loadBlogs();
    });
}

// Show pages
function showHome() {
    document.getElementById('home-page').style.display = 'block';
    document.getElementById('blog-detail-page').style.display = 'none';
    document.getElementById('create-blog-page').style.display = 'none';
    loadBlogs();
}

function showCreateBlog() {
    if (!currentUser) {
        alert('Please login to create a blog');
        showLogin();
        return;
    }
    document.getElementById('home-page').style.display = 'none';
    document.getElementById('blog-detail-page').style.display = 'none';
    document.getElementById('create-blog-page').style.display = 'block';
}

function showBlogDetail(id) {
    currentBlogId = id;
    document.getElementById('home-page').style.display = 'none';
    document.getElementById('blog-detail-page').style.display = 'block';
    document.getElementById('create-blog-page').style.display = 'none';
    loadBlogDetail(id);
    loadComments(id);
}

function showLogin() {
    document.getElementById('login-modal').style.display = 'block';
}

function showRegister() {
    document.getElementById('register-modal').style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Authentication
async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();
        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            currentUser = data.user;
            showUserMenu();
            closeModal('login-modal');
            document.getElementById('login-form').reset();
        } else {
            alert(data.message || 'Login failed');
        }
    } catch (error) {
        alert('Error: ' + error.message);
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        const data = await response.json();
        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            currentUser = data.user;
            showUserMenu();
            closeModal('register-modal');
            document.getElementById('register-form').reset();
        } else {
            alert(data || 'Registration failed');
        }
    } catch (error) {
        alert('Error: ' + error.message);
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    currentUser = null;
    hideUserMenu();
    showHome();
}

// Get auth headers
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// Load blogs
async function loadBlogs(page = 0) {
    currentPage = page;
    const search = document.getElementById('search-input').value;
    const sort = document.getElementById('sort-select').value;
    
    let url = `${API_BASE_URL}/blogs?page=${page}&size=9`;
    if (search) url += `&search=${encodeURIComponent(search)}`;
    if (sort === 'popular') url += `&sort=popular`;

    try {
        const response = await fetch(url);
        const data = await response.json();
        displayBlogs(data.blogs);
        displayPagination(data.currentPage, data.totalPages);
    } catch (error) {
        console.error('Error loading blogs:', error);
    }
}

// Display blogs
function displayBlogs(blogs) {
    const container = document.getElementById('blogs-container');
    if (blogs.length === 0) {
        container.innerHTML = '<p style="text-align: center; padding: 2rem;">No blogs found. Be the first to write one!</p>';
        return;
    }

    container.innerHTML = blogs.map(blog => `
        <div class="blog-card" onclick="showBlogDetail(${blog.id})">
            ${blog.featuredImageUrl ? `<img src="${blog.featuredImageUrl}" alt="${blog.title}">` : ''}
            <h3>${blog.title}</h3>
            <p class="excerpt">${blog.excerpt || blog.content.substring(0, 200)}...</p>
            <div class="blog-meta">
                <span class="blog-author">By ${blog.author.username}</span>
                <div class="blog-stats">
                    <span>👁️ ${blog.viewCount || 0}</span>
                    <span>💬 ${blog.commentCount || 0}</span>
                    <span>❤️ ${blog.likeCount || 0}</span>
                </div>
            </div>
            ${blog.categories && blog.categories.length > 0 ? `
                <div style="margin-top: 1rem;">
                    ${blog.categories.map(cat => `<span class="category-tag">${cat}</span>`).join('')}
                </div>
            ` : ''}
        </div>
    `).join('');
}

// Display pagination
function displayPagination(currentPage, totalPages) {
    const pagination = document.getElementById('pagination');
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';
    if (currentPage > 0) {
        html += `<button onclick="loadBlogs(${currentPage - 1})">Previous</button>`;
    }
    
    for (let i = 0; i < totalPages; i++) {
        if (i === 0 || i === totalPages - 1 || (i >= currentPage - 2 && i <= currentPage + 2)) {
            html += `<button class="${i === currentPage ? 'active' : ''}" onclick="loadBlogs(${i})">${i + 1}</button>`;
        } else if (i === currentPage - 3 || i === currentPage + 3) {
            html += `<span>...</span>`;
        }
    }
    
    if (currentPage < totalPages - 1) {
        html += `<button onclick="loadBlogs(${currentPage + 1})">Next</button>`;
    }
    
    pagination.innerHTML = html;
}

// Load blog detail
async function loadBlogDetail(id) {
    try {
        const headers = getAuthHeaders();
        const response = await fetch(`${API_BASE_URL}/blogs/${id}`, { headers });
        const blog = await response.json();
        displayBlogDetail(blog);
    } catch (error) {
        console.error('Error loading blog:', error);
    }
}

// Display blog detail
function displayBlogDetail(blog) {
    const container = document.getElementById('blog-detail');
    container.innerHTML = `
        ${blog.featuredImageUrl ? `<img src="${blog.featuredImageUrl}" alt="${blog.title}">` : ''}
        <h1>${blog.title}</h1>
        <div class="blog-meta">
            <span class="blog-author">By ${blog.author.username}</span>
            <span>${new Date(blog.createdAt).toLocaleDateString()}</span>
            <div class="blog-stats">
                <span>👁️ ${blog.viewCount || 0}</span>
                <span>💬 ${blog.commentCount || 0}</span>
            </div>
        </div>
        ${blog.categories && blog.categories.length > 0 ? `
            <div style="margin-bottom: 1rem;">
                ${blog.categories.map(cat => `<span class="category-tag">${cat}</span>`).join('')}
            </div>
        ` : ''}
        <div class="content">${blog.content}</div>
        <div class="blog-actions">
            <button class="like-btn ${blog.isLiked ? 'liked' : ''}" onclick="toggleLike(${blog.id})">
                ${blog.isLiked ? '❤️' : '🤍'} Like (${blog.likeCount || 0})
            </button>
        </div>
    `;
}

// Toggle like
async function toggleLike(blogId) {
    if (!currentUser) {
        alert('Please login to like blogs');
        showLogin();
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/likes/blog/${blogId}`, {
            method: 'POST',
            headers: getAuthHeaders()
        });

        const data = await response.json();
        if (response.ok) {
            loadBlogDetail(blogId);
            loadBlogs(currentPage);
        }
    } catch (error) {
        console.error('Error toggling like:', error);
    }
}

// Load comments
async function loadComments(blogId) {
    try {
        const response = await fetch(`${API_BASE_URL}/comments/blog/${blogId}`);
        const comments = await response.json();
        displayComments(comments, blogId);
    } catch (error) {
        console.error('Error loading comments:', error);
    }
}

// Display comments
function displayComments(comments, blogId) {
    const container = document.getElementById('comments-section');
    container.innerHTML = `
        <h3>Comments (${comments.length})</h3>
        ${currentUser ? `
            <div class="comment-form">
                <textarea id="comment-content" placeholder="Write a comment..."></textarea>
                <button class="btn btn-primary" onclick="addComment(${blogId})">Post Comment</button>
            </div>
        ` : '<p>Please <a href="#" onclick="showLogin()">login</a> to comment</p>'}
        <div class="comments-list">
            ${comments.length === 0 ? '<p>No comments yet. Be the first to comment!</p>' : 
                comments.map(comment => `
                    <div class="comment-item">
                        <div class="comment-header">
                            <span class="comment-author">${comment.user.username}</span>
                            <span class="comment-date">${new Date(comment.createdAt).toLocaleString()}</span>
                        </div>
                        <div class="comment-content">${comment.content}</div>
                    </div>
                `).join('')
            }
        </div>
    `;
}

// Add comment
async function addComment(blogId) {
    if (!currentUser) {
        alert('Please login to comment');
        return;
    }

    const content = document.getElementById('comment-content').value;
    if (!content.trim()) {
        alert('Please enter a comment');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/comments`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ blogId, content })
        });

        if (response.ok) {
            document.getElementById('comment-content').value = '';
            loadComments(blogId);
            loadBlogDetail(blogId);
            loadBlogs(currentPage);
        } else {
            alert('Failed to post comment');
        }
    } catch (error) {
        console.error('Error adding comment:', error);
    }
}

// Create blog
async function handleCreateBlog(e) {
    e.preventDefault();
    if (!currentUser) {
        alert('Please login to create a blog');
        return;
    }

    const title = document.getElementById('blog-title').value;
    const content = document.getElementById('blog-content').value;
    const imageUrl = document.getElementById('blog-image').value;
    const categoriesStr = document.getElementById('blog-categories').value;
    const categories = categoriesStr ? categoriesStr.split(',').map(c => c.trim()).filter(c => c) : [];

    try {
        let url = `${API_BASE_URL}/blogs`;
        if (categories.length > 0) {
            url += '?' + categories.map(c => `categories=${encodeURIComponent(c)}`).join('&');
        }

        const response = await fetch(url, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ title, content, featuredImageUrl: imageUrl })
        });

        if (response.ok) {
            alert('Blog published successfully!');
            document.getElementById('create-blog-form').reset();
            showHome();
        } else {
            const error = await response.text();
            alert('Failed to publish blog: ' + error);
        }
    } catch (error) {
        alert('Error: ' + error.message);
    }
}

// Load categories
async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE_URL}/categories`);
        const categories = await response.json();
        displayCategories(categories);
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// Display categories
function displayCategories(categories) {
    const container = document.getElementById('categories-section');
    if (categories.length === 0) return;

    container.innerHTML = `
        <div class="category-tags">
            ${categories.map(cat => `
                <span class="category-tag" onclick="filterByCategory('${cat.name}')">${cat.name}</span>
            `).join('')}
        </div>
    `;
}

// Filter by category (simplified - would need backend support)
function filterByCategory(categoryName) {
    document.getElementById('search-input').value = categoryName;
    loadBlogs(0);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modals = document.getElementsByClassName('modal');
    for (let modal of modals) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    }
}

