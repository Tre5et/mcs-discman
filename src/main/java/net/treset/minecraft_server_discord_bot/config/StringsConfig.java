package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.config.message.Message;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.time.LocalDateTime;
import java.util.List;

public class StringsConfig extends ValidatableConfig {
   public Message<Number> seconds = new Message<>("{number} seconds");
   public String oneSecond = "1 second";
   public Message<Number> minutes = new Message<>("{number} minutes");
   public String oneMinute = "1 minute";
   public Message<Number> hours = new Message<>("{number} hours");
   public String oneHour = "1 hour";
   public Message<Number> days = new Message<>("{number} days");
   public String oneDay = "1 day";
   public Message<Number> months = new Message<>("{number} months");
   public String oneMonth = "1 month";
   public Message<Number> years = new Message<>("{number} years");
   public String oneYear = "1 year";
   public String monday = "Monday";
   public String tuesday = "Tuesday";
   public String wednesday = "Wednesday";
   public String thursday = "Thursday";
   public String friday = "Friday";
   public String saturday = "Saturday";
   public String sunday = "Sunday";
   public String am = "AM";
   public String pm = "PM";
   public Message<String> timeIn = new Message<>("in {string}");
   public Message<String> timeAgo = new Message<>("{string} ago");
   public String january = "January";
   public String february = "February";
   public String march = "March";
   public String april = "April";
   public String may = "May";
   public String june = "June";
   public String july = "July";
   public String august = "August";
   public String september = "September";
   public String october = "October";
   public String november = "November";
   public String december = "December";
   public Message<LocalDateTime> timestampFullWeekday = new Message<>("{weekday_name}, {month_name} {day}, {year} at {hour_12}:{minute} {am_pm}");
   public Message<LocalDateTime> timestampFull = new Message<>("{month_name} {day}, {year} at {hour_12}:{minute} {am_pm}");
   public Message<LocalDateTime> timestampDate = new Message<>("{month_name} {day}, {year}");
   public Message<LocalDateTime> timestampDateShort = new Message<>("{month}/{day}/{year}");
   public Message<LocalDateTime> timestampTimeSeconds = new Message<>("{hour_12}:{minute}:{second} {am_pm}");
   public Message<LocalDateTime> timestampTime = new Message<>("{hour_12}:{minute} {am_pm}");
   public Message<LocalDateTime> timestampShortSeconds = new Message<>("{month}/{day}/{year}, {hour_12}:{minute}:{second} {am_pm}");
   public Message<LocalDateTime> timestampShort = new Message<>("{month}/{day}/{year}, {hour_12}:{minute} {am_pm}");
   public String inGamePrefix = "[Discman]";

    @Override
    public List<String> prefix() {
        return List.of("strings");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        seconds.validate(MessageTemplates.NUMBER);
        require(oneSecond, "oneSecond");
        minutes.validate(MessageTemplates.NUMBER);
        require(oneMinute, "oneMinute");
        hours.validate(MessageTemplates.NUMBER);
        require(oneHour, "oneHour");
        days.validate(MessageTemplates.NUMBER);
        require(oneDay, "oneDay");
        months.validate(MessageTemplates.NUMBER);
        require(oneMonth, "oneMonth");
        years.validate(MessageTemplates.NUMBER);
        require(oneYear, "oneYear");
        require(monday, "monday");
        require(tuesday, "tuesday");
        require(wednesday, "wednesday");
        require(thursday, "thursday");
        require(friday, "friday");
        require(saturday, "saturday");
        require(sunday, "sunday");
        require(am, "am");
        require(pm, "pm");
        timeIn.validate(MessageTemplates.STRING);
        timeAgo.validate(MessageTemplates.STRING);
        require(january, "january");
        require(february, "february");
        require(march, "march");
        require(april, "april");
        require(may, "may");
        require(june, "june");
        require(july, "july");
        require(august, "august");
        require(september, "september");
        require(october, "october");
        require(november, "november");
        require(december, "december");
        timestampFullWeekday.validate(MessageTemplates.INSTANT);
        timestampFull.validate(MessageTemplates.INSTANT);
        timestampDate.validate(MessageTemplates.INSTANT);
        timestampDateShort.validate(MessageTemplates.INSTANT);
        timestampTimeSeconds.validate(MessageTemplates.INSTANT);
        timestampTime.validate(MessageTemplates.INSTANT);
        timestampShortSeconds.validate(MessageTemplates.INSTANT);
        timestampShort.validate(MessageTemplates.INSTANT);
        require(inGamePrefix, "inGamePrefix");
   }
}
