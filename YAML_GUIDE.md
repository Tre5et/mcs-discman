# Mini YAML Guide
YAML is a textual data representation, much like JSON except simpler to read and write. This mini guide is focussed on the features that are used in the manager configuration.

For more complete information, check the [official documentation](https://yaml.org/spec/1.2.2/).

## Structure 
A YAML file is essentially a line separated list of key value pairs. This means each line contains a value of the format
```yaml
key: value
```

The `key` is the name of the config option and the `value` is the setting of the option. 

Most of the time, this value is just simple text, be that a number, a boolean value (`true` / `false`) or any other text.

## Blocks (Nesting)
Options can be nested into blocks. This is done by indenting options below other options.

Consider the configuration:
```yaml
server:
  host: localhost
  port: 25569
```
Here, `server` is the block configuration and `host` and `port` are options in the block.

Blocks may be nested multiple layers deep.

## Lists
Some options require lists of values. Lists can be represented in two ways:
- Flow style:
  ```yaml
  option: [value1, value2, value3]
  ```
- Block style:
  ```yaml
  option:
    - value1
    - value2
    - value3
  ```
These two ways are exactly equivalent and can be interchanged at will.

## (Advanced) Strings
Most string values can simply be defined by writing the text content:
```yaml
option: This is a simple sting value.
```

However, there are cases where this does not work. One important case are message strings with a replacable value at the start. This means, that the sting has to start with `{`, which is not allowed. Further strings must not contain the characters `:` and `#`. There are three ways to define a string like this:
- Single-quoted string:
  ```yaml
  option: '{player} is replaced!'
  ```
- Double-quoted string:
  ```yaml
  option: "{player} is replaced!"
  ```
- Folded line (line breaks are ignored):
  ```yaml
  option: >-
    {player} is
    replaced!
  ```
These representations all produce the string:
```
{player} is replaced!
```

The folded line style is also useful, if you want to write out long strings that should only be output as one line. If you want to include linebreaks, there are two further ways of doing this:
- Double-quoted escaped linebreak `\n`:
  ```yaml
  option: "This is a string\nand this is on a new line."
  ```
- Literal style:
  ```yaml
  option: |
    This is a string
    and this is on a new line.
  ```
Both of these configurations produce the string:
```
This is a string
and this is on a new line.
```

These are the cases where a quoted string, folded line style or literal style is required:
- The string contains `:` or `#`.
- The string starts with `{`, `}`, `[`, `]`, `?`, `!`, `@`, `&`, `|`, `>` or `*`.
- The string is exactly `true`, `false`, `null`, `~` or `.inf`.
- The string looks like a number: `12`, `3.14`, `12e+7`, `0xD4`, `0o8` etc.
- The sting looks like a date: `2026-03-31` etc.
- The string requires spaces at the start or end (quoted strings required).

