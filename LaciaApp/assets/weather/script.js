function applyData(data) {
    try {
        document.getElementById('data_table').innerHTML = data;
    } catch (e) {
        alert(e);
    }
}