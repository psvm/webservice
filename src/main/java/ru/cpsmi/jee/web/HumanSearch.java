package ru.cpsmi.jee.web;

import ru.cpsmi.jee.model.Human;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/* Аннотация, которая обозначает, что этот класс будет использоваться сервлетом
 *  если в дополнение к пути его мэппинга в url будет встречен human */
@Path("/human")
public class HumanSearch {
    //путь к нашему файлу с людьми
    private final String FILE_NAME = "users";
    //коллекция, в которой мы будем хранить людей из файла
    private HashMap<Integer, Human> humanMap;

    /* Если обращаться напрямую к коллекции людей каждый раз, когда она нам нужна
     необходимо будет сначала проверить, а есть ли там что-то и если нет, то инициализировать ее.
     Придется писать много дублирующегося кода и лишних проверок.
     Намного проще создать геттер, который будет возвращать коллекцию, если она инициализирована,
     а если нет - сначала заполнит ее данными. */
    private HashMap<Integer, Human> getHumanMap() {
        //если коллекция не инициализирована
        if(humanMap == null) {
            //создаем пустую мапу
            humanMap = new HashMap<>();
           /* получаем класслоадер, он нам нужен для того, чтобы найти наш файл по пути FILE_NAME
           в runtime
           Подробнее можно почитать тут: https://blogs.oracle.com/vmrobot/entry/основы_динамической_загрузки_классов_в
           или в Idea нажав ctrl, навести мышку на  ClassLoader ниже и лкм по нему.
            */
            ClassLoader cl = this.getClass().getClassLoader();
            try {
               /*Метод readLine() в BufferedReader читает одну строку и возвращает ее, если
                строки закончились, возвращает null
                 */
                BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(FILE_NAME)));
                while (true) {
                    //читаем строку
                    String tmp = br.readLine();
                    //если строки закончились - выходим из цикла
                    if (tmp == null) break;
                    //если нет - создаем нового человека и отправляем его в humanMap с его id в качестве ключа
                    String[] humanArr = tmp.split(",");
                    Human human = new Human();
                    human.setId(Integer.parseInt(humanArr[0]));
                    human.setName(humanArr[1]);
                    human.setZipCode(humanArr[2]);
                    human.setCity(humanArr[3]);
                    humanMap.put(human.getId(), human);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        // в любом случае возвращаем инициализированную мапу
        return humanMap;
    }

    /*
        Аннотация @GET говорит о том, что этот метод будет выполнен по get запросу,
        т.е. браузер просто перейдя по url что-то(что - см. в web.xml)/human/test
        отправит обычный get запрос и получит понятный для него ответ.
        @Path("/test") - думаю, вы уже поняли
        @Produces(MediaType.TEXT_HTML) - говорит, каким "типом данных" мы будем отправлять ответ.
        На самом деле тип данных - всегда строка, но эта строчка будет помогать сервлету формировать header ответа.
     */
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_HTML)
    public Response verifyRESTService(InputStream incomingData) {
       /* Чтобы ответить что-то и заодно проверить, как работает наш метод getHumanMap()
       напишем маленький ответ браузеру - создадим стрингбилдер, запишем туда общее количество
       людей в нашей мапе и добавим каждого из них.
       */

        StringBuilder result = new StringBuilder();
        result.append("Human search service successfully started..<br>");
        result.append("Humans : ").append(getHumanMap().size()).append("<br>");
        for (Human h : getHumanMap().values()) {
            result.append(h.toString()).append("<br>");
        }
        /*вернуть нам надо статус запроса (попробуйте потом 404) и строку с данными из мапы.  */
        return Response.status(200).entity(result.toString()).build();
    }
    /* @Post - в браузере теперь ничего не увидим.
     *  @Produces(MediaType.APPLICATION_JSON) - помогает сформировать правильный header ответа
     * */

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchREST(InputStream incomingData) {
        // В этом билдере мы будем собирать в строку все, что нам прислали в качестве запроса
        StringBuilder builder = new StringBuilder();
        // В этом билдере мы будем собирать json, которые отправится назад
        StringBuilder json = new StringBuilder();
        String separator = "";
        // сразу добавим первый символ к в наш json - сообщаем о том, что это массив
        json.append('[');

   /* Читаем incomingData - параметры post запроса, что передал нам сервлет
           и выводим их на консоль.
    */
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
        }
        System.out.println("Request: " + builder.toString());
   /* Заранее знаем, что нам придет только один параметр - буквы имени человека, по которым надо искать его у нас в базе(humanMap)
   Поэтому сплитим по = и берем второй элемент массива
    */
        String name = builder.toString().split("=")[1];
        if(name != null && !name.isEmpty()) {
            //для всех элементов humanMap, если буквы содержатся в их имени вызываем toString() и кладем их в json-строку
            for (Human h : getHumanMap().values()) {
                if(h.getName().contains(name)){
                    json.append(separator);
                    json.append(h.toString());
                    separator = ",";
                }
            }
        }
        //закрываем json-массив
        json.append(']');
        //на всякий случай выводим его в консоль
        System.out.println("Response: " + json.toString());

        return Response.status(200).entity(json.toString()).build();
    }

}

