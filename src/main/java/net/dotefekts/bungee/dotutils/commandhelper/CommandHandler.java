package net.dotefekts.bungee.dotutils.commandhelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Repeatable;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CommandHandlers.class)
public @interface CommandHandler {
	String command() default "";
	String format() default "";
	String permission() default "";
	boolean serverCommand() default false;
}
