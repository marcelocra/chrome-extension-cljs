var events = {
    INITIALIZE_HISTORY_ITEMS: 'initialize-history-items',
    MOVE_TAB_TO_WINDOW: 'move-tab-to-window',
};

// Data structure used to hold all constants that must be shared between
// background scripts and options page.
chrome.storage.local.set({events: events});

// Register all alarms that the system must listen to.
chrome.alarms.onAlarm.addListener(function (alarm) {
    if (alarm.name === events.INITIALIZE_HISTORY_ITEMS) {
        initializeHistoryItems();
    }
});

chrome.commands.onCommand.addListener(function (command) {
    if (command === events.MOVE_TAB_TO_WINDOW) {
        toggleTabToWindow();
    }
});

chrome.browserAction.onClicked.addListener(function (tab) {
    openOrFocusOptionsPage();
});
