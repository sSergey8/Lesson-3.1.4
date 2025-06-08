
    function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

    // Парсит список ролей
    function parseRoles(rolesRaw) {
    try {
    return JSON.parse(rolesRaw.replace(/'/g, '"'));
} catch (e) {
    return rolesRaw.replace(/[\[\]]/g, '').split(',').map(role => {
    const name = role.trim().replace("ROLE_", "");
    return { name: name };
});
}
}

    // Устанавливает выбранные опции в <select> (мультисписке) ролей
    function setSelectedRoles(selectElement, roles) {
    Array.from(selectElement.options).forEach(option => option.selected = false);
    roles.forEach(role => {
    Array.from(selectElement.options).forEach(option => {
    if (option.text === role.name || option.text === role.name.replace("ROLE_", "")) {
    option.selected = true;
}
});
});
}

     // Заполняет модальные формы "edit" или "delete"
    function fillModalForm(modalPrefix, data) {
    document.getElementById(`${modalPrefix}UserId`).value = data.id;
    document.getElementById(`${modalPrefix}UserIdDisplay`).value = data.id;
    document.getElementById(`${modalPrefix}FirstName`).value = data.firstname;
    document.getElementById(`${modalPrefix}LastName`).value = data.lastname;
    document.getElementById(`${modalPrefix}Age`).value = data.age;
    document.getElementById(`${modalPrefix}Email`).value = data.email;

    if (modalPrefix === 'delete') {
    const roles = parseRoles(data.roles);
    document.getElementById('deleteRole').value = roles.length > 0 ? roles[0].name : '';
} else {
    const roles = parseRoles(data.roles);
    setSelectedRoles(document.getElementById(`${modalPrefix}Roles`), roles);
}
}


    // Переключает вкладки
    function showTab(tabName) {
    const showTable = tabName === 'usersTable';
    document.getElementById('usersTableDiv').style.display = showTable ? '' : 'none';
    document.getElementById('newUserFormDiv').style.display = showTable ? 'none' : '';
    document.getElementById('showUsersBtn').classList.toggle('active', showTable);
    document.getElementById('showNewUserFormBtn').classList.toggle('active', !showTable);

    if (!showTable) {
    resetFormErrors();
}
}

    function showAllUsers() {
    document.querySelectorAll('#usersTableDiv tbody tr').forEach(row => {
        row.style.display = '';
    });
}

    // Фильтрует таблицу, показывает только пользователей ADMIN.
    function showAdminUsersOnly() {
    document.querySelectorAll('#usersTableDiv tbody tr').forEach(row => {
        const rolesText = row.querySelector('td:nth-child(6)').textContent;
        if (rolesText.includes('ADMIN')) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

    // Очищает ошибки формы
    function resetFormErrors() {
    const emailField = document.getElementById('email');
    if (emailField) emailField.classList.remove('is-invalid');
    document.querySelectorAll('.error-message').forEach(msg => msg.style.display = 'none');
}

    // Инициализация и обработчики событий
    document.addEventListener('DOMContentLoaded', function() {

    if (/*[[${emailError}]]*/ false) {
    document.getElementById('email').focus();
}

    // Переключает на вкладку с таблицей пользователей
    document.getElementById('showUsersBtn').addEventListener('click', e => {
    e.preventDefault();
    showTab('usersTable');
});

    // Переключает на вкладку "создание нового пользователя"
        document.getElementById('showNewUserFormBtn').addEventListener('click', e => {
            e.preventDefault();
            showTab('newUserForm');
            // Очистка полей формы нового пользователя
            document.getElementById('firstName').value = '';
            document.getElementById('lastName').value = '';
            document.getElementById('age').value = '';
            document.getElementById('email').value = '';
            document.getElementById('password').value = '';

            const rolesSelect = document.getElementById('roles');
            if (rolesSelect) {
                Array.from(rolesSelect.options).forEach(option => option.selected = false);
            }
            resetFormErrors();
        });

    // Включает "админский вид"
    document.getElementById('adminViewBtn').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('usersTableDiv').classList.remove('user-view');

    this.classList.add('active');
    document.getElementById('userViewBtn').classList.remove('active');

    showAllUsers();
});

    // Включает "пользовательский вид"
    document.getElementById('userViewBtn').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('usersTableDiv').classList.add('user-view');

    this.classList.add('active');
    document.getElementById('adminViewBtn').classList.remove('active');

    showAdminUsersOnly();
});

    // Обработчики кнопки Delete. Открывает модалку удаления
    document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', function() {
    const data = {
    id: this.getAttribute('data-id'),
    firstname: this.getAttribute('data-firstname'),
    lastname: this.getAttribute('data-lastname'),
    age: this.getAttribute('data-age'),
    email: this.getAttribute('data-email'),
    roles: JSON.stringify([{name: this.getAttribute('data-role')}])
};
    fillModalForm('delete', data);
    document.getElementById('deleteModal').style.display = 'flex';
});
});

    // // Обработчики кнопки edit. Открывает модалку редактирования
    document.querySelectorAll('.edit-btn').forEach(btn => {
    btn.addEventListener('click', function() {
    const data = {
    id: this.getAttribute('data-id'),
    firstname: this.getAttribute('data-firstname'),
    lastname: this.getAttribute('data-lastname'),
    age: this.getAttribute('data-age'),
    email: this.getAttribute('data-email'),
    roles: this.getAttribute('data-roles')
};
    fillModalForm('edit', data);
    document.getElementById('editModal').style.display = 'flex';
});
});

    // Закрытие модальных окон по клику вне формы
//     window.addEventListener('click', e => {
//     if (e.target.classList.contains('modal-overlay')) {
//     e.target.style.display = 'none';
// }
// });

});