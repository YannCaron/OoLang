package net.oolang.poc;

import net.oolang.ast.Ast;
import net.oolang.ast.AstDefault;
import net.oolang.ast.AstType;
import net.oolang.ast.Context;
import net.oolang.ast.ExecutionEntryPoint;
import net.oolang.ast.Symbol;
import net.oolang.ast.Visitor;

public class Main {

    private static class PrintContext extends Context {

        private final StringBuilder builder = new StringBuilder();

        public StringBuilder getBuilder() {
            return builder;
        }

        @Override
        public String toString() {
            return builder.toString();
        }

    }

    public static void main(String[] args) {

        AstType NUMBER = new AstType("Number");
        AstType OPERATION = new AstType("Addition");

        Ast left = new AstDefault(NUMBER, new Symbol("1"));
        Ast right = new AstDefault(NUMBER, new Symbol("7"));
        Ast addition = new AstDefault(OPERATION, new Symbol("+"), left, right);

        Ast subRight = new AstDefault(NUMBER, new Symbol("8"));
        Ast substraction = new AstDefault(OPERATION, new Symbol("-"), addition, subRight);

        Visitor<PrintContext> printVisitor = new Visitor<PrintContext>() {
            @Override
            public PrintContext instancingContext() {
                return new PrintContext();
            }

            @Override
            public void registerVisitors() {
                registerDefault(NUMBER, (AstDefault ast) -> {
                    getContext().getBuilder().append(ast.getSymbol().getValue());
                });

                registerDefault(OPERATION, (AstDefault ast) -> {
                    ast.visiteChild(0);
                    getContext().getBuilder().append(' ');
                    getContext().getBuilder().append(ast.getSymbol().getValue());
                    getContext().getBuilder().append(' ');
                    ast.visiteChild(1);
                });
            }
        };

        ExecutionEntryPoint<PrintContext> ep = new ExecutionEntryPoint<>(printVisitor, substraction);

        ep.execute();
        System.out.println(ep.getContext().toString());

        ep.execute();
        System.out.println(ep.getContext().toString());

        System.out.println(
          new ExecutionEntryPoint<>(printVisitor, substraction).execute().getContext().toString()
        );

    }
}
