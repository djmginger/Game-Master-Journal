# Game-Master-Journal

This application was created in order to provide an efficient and streamlined way to store, edit, and view information on a Tabletop Roleplaying game. This is especially helpful for Game Masters, as it provides an easy-to-use and organized way to keep track of the many details of their game.

I chose to write this project in android Java, mostly due to my prior familiarity with Java, and not knowing much about Kotlin at the beginning of the project. Almost all of the challenges faced with this project were born out of unfamiliarty with Android development, as this project was my motivation to learn how to develop for a mobile platform.

Some of the most notable obstacles to overcome was having to figure out how to store photos/audio files within the built in SQLite database, creating import and export functionality for the various databases, as well as implementing a built in langauge selection.

STARTING ACTIVITY
------------------------------------------------
The main activity acts as the landing page, and sets up all the buttons that act as portals to the rest of the application. The basic design of these activites are detailed in the next section.

Within the main activity, there exists the Dark Mode button (themeSwitch) that, once purchased, creates a theme and writes it to the users SharedPreferences file for the app, ensuring that the change lasts after a user closes the app. On creation, every activity checks the theme, and switches the colors between Light and Dark versions.

The main activity is also where a user may change their langauge option (via the language button). There are localized string resources for English (default), Spanish, German, French, Japanese, and Russian. The locale is updated to reflect whatever a user selects, and applies the locale change for the entire app (this is also writted into SharedPreferences, similar to the Dark Mode theme)

From the main activity, a user may also select the Option page, which is a way for them to customize the applications look and theme to some degree. They can change the name of each section, as well as its main color and infobox colors. This then gets applied and is reflected in all sections that the user has updated.

The final option within the main activity is a button that brings the user to a Database Management page. Here a user can export the data from any of the specific databases, which are exported as a csv file. The user can also import a csv file as well, if they prefer to do all of their creation outside of the app. The imported file will need to be formatted properly, and they user is urged to export a database with some information first, so they can understand how to properly format the file they wish to import. Finally, the user can wipe all data from any of the various databases, or reset the preset options found within some activities.

BASIC APP STRUCTURE
------------------------------------------------
All of the central activites that are used to view, edit, and list various data (NPCS, Loot, Cities, Locations, Misc.) all have the same basic framework in mind.

It starts with a simple List page (LootList, CityList, etc.). This list is pulled from the SQLite databases, and put into a recycler view. When a user selects the + button at the bottom to add an item, they're brought to the corresponding Info page (LootInfo, CityInfo, etc.) Most of these list pages have filters for certain variables. The user is able to filter NPCs by specific Cities, or Items by Item groups for example, which makes sorting through large amounts of entires much easier.

The Info page (LootInfo, CityInfo, etc.) is where all user editing is done. There are various text fields for the user to fill, sometimes drop down menus, checkboxes, as well as an image selection option. Once a user has finished editing the item and saves it, all of this information is formatted (if necessary, in the case of things like images or audio files), and stored within an SQLite database that corresponds to that activity. Each section has its own DBHelper file (lootDBHelper, cityDBHelper, etc.) that handles table creation and defines all SQLite commands used within the app. There also exists an AutoSave feature within each Info activity, that detects if a user exits the Info activity after having made changes, but they forgot to save the item. The user is asked if they'd like to save, or simply exit.

The Display page (LootDisplay, CityDisplay, etc.) is where a user is brought when they selection an item from the recyclerview of the inital list of items, rather than selecting to add a new item. The display page is straightforward, as it takes the information stored within the SQLite database, formats it to look presentable, and displays it in an un-editable format for the user. If any information is linked, for example if an NPC is tied to a City that a user has created, then the display page will make that listed city a clickable link that brings the user to that specific entry.

ACTIVITY SPECIFIC FUNCTIONALITY
------------------------------------------------
There are some unique features found within certain activities, that are described in basic detail here.

When creating an NPC, within the NpcInfo page, there is an option to add a "voice" for the character. This allows the user to select an audio file from their devices library. When they select an audio file, it is stored and referenced by the app. The app then creates a basic media player, complete with Stop, Play, and Pause buttons, allowing the user to interact with the audio. The player is also added to the corresponding display page. The idea behind this is if a user creates an accent or voice for an NPC, they can easily upload it and reference it for future sessions without forgetting what the character sounded like.

Within both the NpcInfo and CityInfo activites, there exist certian fields that use a list of presets: race, envrionment, economy, etc. When a user goes to choose one of these values, they're brought to a page listing all preset selections for that value, and can add their own preset selections, or delete current ones if they don't have a use for it.

The Miscellaneous activity is the most complex, as it incorperates almost all features of the other basic sections, and is almost entirely customizable for the user. The user can change the title of the 4 different text fields, they can create a group that helps for filtering purposes if a user wants to create a group of objects. They can link created NPCS, Cities, Items, or entire Item groups, and all of these linked entries become clickable once a user views it from the Display page, creating a web of information that can all be referenced directly from the entry within this custom page.
