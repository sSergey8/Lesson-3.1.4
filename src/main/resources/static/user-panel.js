document.addEventListener("DOMContentLoaded", function() {
    // Элементы навигации ролей
    const adminViewBtn = document.getElementById('adminViewBtn');
    const userViewBtn = document.getElementById('userViewBtn');
    const body = document.body;

    // Кнопки вкладок
    const showUsersBtn = document.getElementById('showUsersBtn');
    const showNewUserFormBtn = document.getElementById('showNewUserFormBtn');

    // Основные блоки
    const usersTableDiv = document.getElementById('usersTableDiv');
    const newUserFormDiv = document.getElementById('newUserFormDiv');

    // Таблица и формы
    const usersTableBody = usersTableDiv.querySelector('tbody');
    const userForm = document.getElementById('userForm');
    const rolesSelect = document.getElementById('roles');
    const emailErrorDiv = document.getElementById('emailError');

    // Модалка редактирования
    const editUserModal = document.getElementById('editUserModal');
    const editModalClose = document.getElementById('editModalClose');
    const editUserForm = document.getElementById('editUserForm');
    const editId = document.getElementById('editId');
    const editIdDisplay = document.getElementById('editIdDisplay');
    const editFirstName = document.getElementById('editFirstName');
    const editLastName = document.getElementById('editLastName');
    const editAge = document.getElementById('editAge');
    const editEmail = document.getElementById('editEmail');
    const editPassword = document.getElementById('editPassword');
    const editRoles = document.getElementById('editRoles');
    const editUserCancelBtn = document.getElementById('editUserCancelBtn');

    // Модалка удаления
    const deleteUserModal = document.getElementById('deleteUserModal');
    const deleteModalClose = document.getElementById('deleteModalClose');
    const deleteUserForm = document.getElementById('deleteUserForm');
    const deleteId = document.getElementById('deleteId');
    const deleteVisibleId = document.getElementById('deleteVisibleId');
    const deleteFirstName = document.getElementById('deleteFirstName');
    const deleteLastName = document.getElementById('deleteLastName');
    const deleteAge = document.getElementById('deleteAge');
    const deleteEmail = document.getElementById('deleteEmail');
    const deleteRoles = document.getElementById('deleteRoles');
    const deleteCancelBtn = document.getElementById('deleteCancelBtn');

    let allRoles = []; // Хранение ролей
    let allUsers = []; // Хранение пользователей
    let userIdToDelete = null; // ID пользователя для удаления

    // Загрузка ролей
    async function loadRoles() {
        try {
            const response = await axios.get('/api/admin/roles');
            allRoles = response.data;
            populateRolesSelect(rolesSelect, allRoles);
            populateRolesSelect(editRoles, allRoles);
        } catch (error) {
            console.error('Ошибка загрузки ролей:', error);
        }
    }

    // Заполнение select ролей
    function populateRolesSelect(selectElement, roles) {
        selectElement.innerHTML = '';
        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.id;
            option.textContent = role.name.replace('ROLE_', '');
            selectElement.appendChild(option);
        });
    }

    // Загрузка пользователей
    async function loadUsers() {
        try {
            const response = await axios.get('/api/admin/users');
            allUsers = response.data;
            renderUsersTable(allUsers);
        } catch (error) {
            console.error('Ошибка загрузки пользователей:', error);
        }
    }

    // Отрисовка таблицы
    function renderUsersTable(users) {
        usersTableBody.textContent = '';

        users.forEach(user => {
            const tr = document.createElement('tr');

            tr.innerHTML = `
                <td>${user.id}</td>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${user.roles.map(r => r.name.replace('ROLE_', '')).join(', ')}</td>
                <td class="action-column">
                    <button class="btn btn-sm btn-primary edit-btn" data-id="${user.id}">Edit</button>
                </td>
                <td class="action-column">
                    <button class="btn btn-sm btn-danger delete-btn" data-id="${user.id}">Delete</button>
                </td>
            `;

            usersTableBody.appendChild(tr);
        });

        // Привязка событий к кнопкам
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', openEditModal);
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', openDeleteModal);
        });
    }

    // Показать модалку
    function showModal(modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    // Скрыть модалку
    function hideModal(modal) {
        modal.style.display = 'none';
        document.body.style.overflow = '';
    }

    // Закрытие модалки редактирования
    editModalClose.addEventListener('click', () => hideModal(editUserModal));
    editUserCancelBtn.addEventListener('click', () => hideModal(editUserModal));
    editUserModal.addEventListener('click', e => {
        if (e.target === editUserModal) hideModal(editUserModal);
    });

    // Закрытие модалки удаления
    deleteModalClose.addEventListener('click', () => hideModal(deleteUserModal));
    deleteCancelBtn.addEventListener('click', () => hideModal(deleteUserModal));
    deleteUserModal.addEventListener('click', e => {
        if (e.target === deleteUserModal) hideModal(deleteUserModal);
    });

    // Отправка формы создания пользователя
    userForm.addEventListener('submit', async e => {
        e.preventDefault();
        emailErrorDiv.style.display = 'none';

        const formData = new FormData(userForm);
        const roles = Array.from(rolesSelect.selectedOptions).map(opt => ({id: opt.value}));

        const newUser = {
            firstName: formData.get('firstname'),
            lastName: formData.get('lastname'),
            age: +formData.get('age'),
            email: formData.get('email'),
            password: formData.get('password'),
            roles: roles
        };

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        try {
            await axios.post('/api/admin/users', newUser, {
                headers: {
                    [csrfHeader]: csrfToken
                }
            });
            await loadUsers();
            userForm.reset();
            showUsersView();
        } catch (error) {
            if (error.response && error.response.data && error.response.data.message) {
                emailErrorDiv.textContent = error.response.data.message;
                emailErrorDiv.style.display = 'block';
            } else {
                console.error('Ошибка при создании пользователя:', error);
            }
        }
    });

    // Переключение видов (Admin/User)
    adminViewBtn.addEventListener('click', e => {
        e.preventDefault();
        adminViewBtn.classList.add('active');
        userViewBtn.classList.remove('active');
        body.classList.remove('user-view');
        showUsersView();
    });

    userViewBtn.addEventListener('click', e => {
        e.preventDefault();
        userViewBtn.classList.add('active');
        adminViewBtn.classList.remove('active');
        body.classList.add('user-view');
        showUsersView();
    });

    // Показ/скрытие колонок с действиями
    function showActions(show) {
        const actionColumns = document.querySelectorAll('.action-column');
        actionColumns.forEach(col => {
            col.style.display = show ? 'table-cell' : 'none';
        });
    }

    // Показ таблицы пользователей
    showUsersBtn.addEventListener('click', e => {
        e.preventDefault();
        showUsersView();
    });

    // Показ формы добавления пользователя
    showNewUserFormBtn.addEventListener('click', e => {
        e.preventDefault();
        showNewUserForm();
    });

    // Функции переключения между табами
    function showUsersView() {
        usersTableDiv.style.display = 'block';
        newUserFormDiv.style.display = 'none';

        showUsersBtn.classList.add('active');
        showNewUserFormBtn.classList.remove('active');

        if (body.classList.contains('user-view')) {
            // Фильтруем пользователей по роли admin
            const admins = allUsers.filter(user =>
                user.roles.some(role => role.name === 'ROLE_ADMIN')
            );
            renderUsersTable(admins);
            showActions(false);
        } else {
            renderUsersTable(allUsers);
            showActions(true);
        }
    }

    function showNewUserForm() {
        usersTableDiv.style.display = 'none';
        newUserFormDiv.style.display = 'block';

        showUsersBtn.classList.remove('active');
        showNewUserFormBtn.classList.add('active');
        userForm.reset();

        emailErrorDiv.style.display = 'none';
        Array.from(rolesSelect.options).forEach(option => option.selected = false);
    }

    // Инициализация
    (async function init() {
        await loadRoles();
        await loadUsers();
        await loadCurrentUser(); // 👈 добавили сюда
        showUsersView();
        showActions(true);
    })();

    // Загрузка текущего пользователя и отображение в navbar
    async function loadCurrentUser() {
        try {
            const response = await axios.get('/api/admin/current');
            const user = response.data;
            const roles = user.roles?.map(role => role.name.replace('ROLE_', '')).join(', ') || 'none';
            document.getElementById('currentUserInfo').textContent = `${user.email} with roles: ${roles}`;
        } catch (error) {
            console.error('Ошибка при загрузке текущего пользователя:', error);
            document.getElementById('currentUserInfo').textContent = 'Ошибка загрузки';
        }
    }

    // Открыть модалку редактирования
    function openEditModal(event) {
        const userId = event.target.dataset.id;
        const user = allUsers.find(u => u.id == userId);
        if (!user) return;

        editId.value = user.id;
        editIdDisplay.value = user.id;
        editFirstName.value = user.firstName;
        editLastName.value = user.lastName;
        editAge.value = user.age;
        editEmail.value = user.email;
        editPassword.value = '';

        Array.from(editRoles.options).forEach(option => {
            option.selected = user.roles.some(r => r.id == option.value);
        });

        showModal(editUserModal);
    }

// Отправка формы редактирования пользователя
    editUserForm.addEventListener('submit', async e => {
        e.preventDefault();

        const id = editId.value;
        const roles = Array.from(editRoles.selectedOptions).map(opt => ({ id: opt.value }));

        const updatedUser = {
            id: +id,
            firstName: editFirstName.value,
            lastName: editLastName.value,
            age: +editAge.value,
            email: editEmail.value,
            roles: roles,
            ...(editPassword.value ? { password: editPassword.value } : {})
        };

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            await axios.put(`/api/admin/users/${id}`, updatedUser, {
                headers: {
                    [csrfHeader]: csrfToken
                }
            });

            await loadUsers();
            hideModal(editUserModal);
        } catch (error) {
            console.error('Ошибка при обновлении пользователя:', error);
        }
    });




    // Открыть модалку удаления
    function openDeleteModal(event) {
        userIdToDelete = event.target.dataset.id;
        const user = allUsers.find(u => u.id == userIdToDelete);
        if (!user) return;

        // Заполняем поля
        deleteVisibleId.value = user.id;
        deleteId.value = user.id;
        deleteFirstName.value = user.firstName;
        deleteLastName.value = user.lastName;
        deleteAge.value = user.age;
        deleteEmail.value = user.email;
        deleteRoles.value = user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');

        showModal(deleteUserModal);
    }

// Отправка формы удаления пользователя
    deleteUserForm.addEventListener('submit', async e => {
        e.preventDefault();
        if (!userIdToDelete) return;

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            await axios.delete(`/api/admin/users/${userIdToDelete}`, {
                headers: {
                    [csrfHeader]: csrfToken
                }
            });
            await loadUsers();
            hideModal(deleteUserModal);
            userIdToDelete = null;
        } catch (error) {
            console.error('Ошибка при удалении пользователя:', error);
        }
    });
});