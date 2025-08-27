package string_template;


public class string_template_main {
    public static void main(String[] args) {
        var nome = "Andrei";
        var idade = 22;

        var dado = STR."Meu nome é \{nome}, tenho \{idade} anos";

        System.out.println(dado);
    }
}
