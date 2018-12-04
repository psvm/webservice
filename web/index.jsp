<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title>RESTful web service</title>

  <!-- CSS CDN from http://getbootstrap.com/getting-started/ -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

</head>

<body>
<div class="container">

  <section class="row" id="main-content">

    <h2>Web service js client</h2>

    <div class="row">
      <div class="col-md-6">

        <div class="form-group">
          <label for="search_name">Введите имя для поиска</label>
          <%--Поле ввода имени для поиска--%>
          <input type="text" class="form-control" id="search_name" placeholder="Имя">
        </div>
        <button id="search_ok" type="button" class="btn btn-default">Искать</button>
        <div>
          <%--Таблица с результатами--%>
          <table id="human-list" class="table table-hover">
            <thead>
            <tr>
              <th>Id</th>
              <th>Имя</th>
              <th>Индекс</th>
              <th>Город</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
          </table>
        </div>

      </div>
      <div class="col-md-6">

      </div>

    </div>
  </section>
</div>
<!-- javascript -->
<%--Подключаем jquery--%>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript">
    //Запоминаем в переменную таблицу, куда будем добавлять найденных людей
    var humanTable = $("#human-list").find("tbody");

    /*Создаем функцию, которая принимает один объект (человека) и выводит одну строку таблицы с ним*/
    function printHuman(el) {
        humanTable.append('<tr>' +
            '<th scope="row">' + el.id + '</th>' +
            '<td>' + el.name + '</td>' +
            '<td>' + el.zipCode + '</td>' +
            '<td>' + el.city + '</td>' +
            '</tr>' +
            '<tr>');
    }
    /*Если кликаем по кнопке Искать с id=search_ok будет срабатывать эта функция*/
    $("#search_ok").click(function () {
        //получаем текст из поля для ввода
        var name = $("#search_name").val();
        //отправляем post запрос на url нашего вебсервиса
        $.post("/api/human/search",
            {
                name: name
            }
        ).done(function (data) {
            //если ответ пришел, будет вызвана эта функция
            //выводим ответ в консоль браузера
            console.log(data);
            //очищаем таблицу от прошлых результатов
            humanTable.empty();
            //простой перебор foreach с вызовом анонимной функции для каждого элемента массива
            data.forEach(function (el, i, data) {
                printHuman(el);
            });
        })

    });

</script>
</body>
</html>