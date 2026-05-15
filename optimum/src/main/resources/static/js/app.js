/* === API Client === */
const API = {
    async request(method, path, body = null) {
        const headers = { 'Content-Type': 'application/json' };
        const csrfToken = document.querySelector('input[name="_csrf"]')?.value;
        if (csrfToken) headers['X-CSRF-TOKEN'] = csrfToken;

        const opts = { method, headers };
        if (body) opts.body = JSON.stringify(body);
        
        const res = await fetch(path, opts);
        if (!res.ok) {
            const err = await res.json().catch(() => ({ message: 'Erreur réseau' }));
            throw new Error(err.message || `HTTP ${res.status}`);
        }
        if (res.status === 204) return null;
        return res.json().catch(() => null);
    },
    get: (path) => API.request('GET', path),
    post: (path, body) => API.request('POST', path, body),
    put: (path, body) => API.request('PUT', path, body),
    delete: (path) => API.request('DELETE', path),
};

/* === Toast Notifications === */
const Toast = {
    container: null,
    init() {
        if (this.container) return;
        this.container = document.createElement('div');
        this.container.className = 'toast-container';
        document.body.appendChild(this.container);
    },
    show(message, type = 'info') {
        this.init();
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `<span>${message}</span>`;
        this.container.appendChild(toast);
        setTimeout(() => toast.remove(), 3500);
    },
    success: (msg) => Toast.show(msg, 'success'),
    error: (msg) => Toast.show(msg, 'error'),
};

const searchInput = document.querySelector("#globalSearch");
const suggestions = document.querySelector("#suggestions");
/* === Notifications === */
const Notifications = {
    async updateBadge() {
        try {
            const data = await API.get('/api/notifications');
            const badge = document.getElementById('notifBadge');
            if (badge) {
                const count = data.unreadCount || 0;
                badge.textContent = count;
                badge.style.display = count > 0 ? 'block' : 'none';
            }
        } catch (_) {}
    }
};

/* === Watchlist Global Toggle === */
// ... (reste du code)
document.addEventListener('click', async (e) => {
    const btn = e.target.closest('[data-watchlist-toggle]');
    if (!btn) return;
    
    e.preventDefault();
    const titleId = btn.dataset.watchlistToggle;
    const isActive = btn.classList.contains('active');
    
    try {
        if (isActive) {
            await API.delete(`/api/watchlist/${titleId}`);
            btn.classList.remove('active');
            Toast.show('Retiré de Ma Liste');
        } else {
            await API.put(`/api/watchlist/${titleId}`);
            btn.classList.add('active');
            Toast.success('Ajouté à Ma Liste');
        }
    } catch (err) {
        Toast.error('Erreur : ' + err.message);
    }
});
const savedTheme = localStorage.getItem("optimum-theme");
if (savedTheme === "prime") {
    document.body.classList.add("theme-prime");
}

const themeToggle = document.querySelector("#themeToggle");
if (themeToggle) {
    themeToggle.textContent = document.body.classList.contains("theme-prime") ? "Netflix" : "Prime";
    themeToggle.addEventListener("click", () => {
        document.body.classList.toggle("theme-prime");
        const isPrime = document.body.classList.contains("theme-prime");
        localStorage.setItem("optimum-theme", isPrime ? "prime" : "netflix");
        themeToggle.textContent = isPrime ? "Netflix" : "Prime";
    });
}

const passwordInput = document.querySelector("#passwordInput");
if (passwordInput) {
    const rules = {
        length: value => value.length >= 8,
        upper: value => /[A-Z]/.test(value),
        lower: value => /[a-z]/.test(value),
        digit: value => /\d/.test(value),
        special: value => /[^A-Za-z0-9]/.test(value)
    };
    passwordInput.addEventListener("input", () => {
        const value = passwordInput.value;
        Object.entries(rules).forEach(([name, test]) => {
            const item = document.querySelector(`[data-rule="${name}"]`);
            if (item) {
                item.classList.toggle("valid", test(value));
            }
        });
    });
}

if (searchInput && suggestions) {
    let timer;
    searchInput.addEventListener("input", () => {
        clearTimeout(timer);
        const query = searchInput.value.trim();
        if (query.length < 2) {
            suggestions.innerHTML = "";
            return;
        }
        timer = setTimeout(async () => {
            const response = await fetch(`/api/search/suggestions?q=${encodeURIComponent(query)}`);
            const data = await response.json();
            suggestions.innerHTML = data.suggestions
                .map(item => `<a href="/search-ui?q=${encodeURIComponent(item.text)}">${item.text}</a>`)
                .join("");
        }, 160);
    });
}

const watchButton = document.querySelector("#watchButton");
if (watchButton) {
    watchButton.addEventListener("click", async () => {
        const titleId = watchButton.dataset.titleId;
        const profileId = watchButton.dataset.profileId;
        const response = await fetch("/api/playback/sessions", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                contentId: titleId,
                contentType: "MOVIE",
                profileId,
                drmSystem: "WIDEVINE",
                videoQuality: "AUTO",
                deviceId: "browser-demo",
                resumeFromSeconds: 0
            })
        });
        document.querySelector("#sessionResult").textContent = JSON.stringify(await response.json(), null, 2);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    Notifications.updateBadge();
});

const profileForm = document.querySelector("#profileForm");
if (profileForm) {
    profileForm.addEventListener("submit", async event => {
        event.preventDefault();
        const form = new FormData(profileForm);
        const response = await fetch("/api/profiles", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                name: form.get("name"),
                isKid: form.get("isKid") === "on"
            })
        });
        document.querySelector("#profileResult").textContent = JSON.stringify(await response.json(), null, 2);
    });
}
