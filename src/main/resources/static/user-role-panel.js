document.addEventListener("DOMContentLoaded", async function () {
    const userTableBody = document.getElementById('userTableBody');

    async function loadUsers() {
        try {
            const response = await axios.get('/api/user');
            const users = response.data;

            renderUsersTable(users);
        } catch (error) {
            console.error('Ошибка загрузки пользователей:', error);
            userTableBody.innerHTML = '<tr><td colspan="6">Не удалось загрузить пользователей</td></tr>';
        }
    }

    function renderUsersTable(users) {
        userTableBody.innerHTML = '';

        if (users.length === 0) {
            userTableBody.innerHTML = '<tr><td colspan="6">Пользователи не найдены</td></tr>';
            return;
        }

        users.forEach(user => {
            const tr = document.createElement('tr');

            tr.innerHTML = `
                <td>${user.id}</td>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${user.roles.map(role => role.name.replace('ROLE_', '')).join(', ')}</td>
            `;

            userTableBody.appendChild(tr);
        });
    }

    // Получение текущего пользователя
    async function loadCurrentUser() {
        try {
            const response = await axios.get('/api/user/current');
            const user = response.data;
            const roles = user.roles?.map(role => role.name.replace('ROLE_', '')).join(', ') || 'none';
            document.getElementById('currentUserInfo').textContent = `${user.email} with roles: ${roles}`;
        } catch (error) {
            console.error('Ошибка при загрузке текущего пользователя:', error);
            document.getElementById('currentUserInfo').textContent = 'Ошибка загрузки';
        }
    }

    await loadCurrentUser();
    await loadUsers();
});
