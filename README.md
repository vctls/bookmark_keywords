# Bookmark Keywords

## Objective

This Android app attempts to replicate the behaviour of
Firefox [bookmark keywords](https://www-archive.mozilla.org/docs/end-user/keywords.html),
a little known, barely documented feature of the browser, that was dropped entirely
from the mobile version and could as well be removed from the desktop version.

On desktop, this feature can already be replaced using a launcher like Synapse on Linux,
Keypirinha on Windows or QuickSilver on MacOS. Sadly, none of the equivalent apps on Android,
like Conjure, seem to have this feature.

## Usage

1. Add a bookmark with a URL template and a keyword.
Use the placeholder `%s` in the URL where you want your search string to go, for example:
`https://duckduckgo.com/%s`
2. In the query input, type the keyword followed by a space then the phrase you want to search.
3. Tap `Go`.

### Remarks

Keywords _must_ be unique.

Templates _do not have to_ have a placeholder. You can use the bookmarks as simple shortcuts.
In that case, whatever you type after the keyword will be ignored.

The app will attempt to open the URL with whatever the default application is for the given
protocol. If the URL starts with `https://`, it will most likely open in your default browser
(which should be Firefox), but the template can be anything you want.

## Why not use DuckDuckGo's “bangs”?

Even if you use a native search bar, bangs will open the DuckDuckGo website first before
redirecting to the corresponding site.
They are less customizable.
Bangs require you to type an exclamation mark immediately followed by the keyword, but some Android
keyboards,like SwiftKey, automatically add a space after punctuation, so you would need to either
enter backspace everytime, disable that feature on your keyboard, or somehow use an URL type input
field where this feature is usually disabled, but that's a choice for the developers to make
on whatever search bar or browser you are using.

## TODO

- [ ] Import bookmarks from Firefox or launcher apps.
- [ ] Import an URL through the _share_ button of another app.
- [ ] Allow searching for bookmarks.
- [ ] Add tags, as in Firefox, to make searching easier.
- [ ] Suggest keyword autocompletion when typing in the query input.
- [ ] Allow multiple templates in order to search on multiple sites at once.