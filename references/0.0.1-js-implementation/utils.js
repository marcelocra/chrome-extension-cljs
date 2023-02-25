/**
 * Get the current URL.
 *
 * @param {function(string)} callback - called when the URL of the current tab
 *   is found.
 */
function getCurrentTab(callback) {

    // Query filter to be passed to chrome.tabs.query - see
    // https://developer.chrome.com/extensions/tabs#method-query
    var queryInfo = {
        active: true,
        currentWindow: true
    };

    chrome.tabs.query(queryInfo, function useTabsInfo(tabs) {
        // chrome.tabs.query invokes the callback with a list of tabs that match the
        // query. When the popup is opened, there is certainly a window and at least
        // one tab, so we can safely assume that |tabs| is a non-empty array.
        // A window can only have one active tab at a time, so the array consists of
        // exactly one tab.
        var tab = tabs[0];

        callback(tab);
    });

    // Most methods of the Chrome extension APIs are asynchronous. This means that
    // you CANNOT do something like this:
    //
    // var url;
    // chrome.tabs.query(queryInfo, function(tabs) {
    //   url = tabs[0].url;
    // });
    // alert(url); // Shows "undefined", because chrome.tabs.query is async.
}

function initializeHistoryItems() {
    chrome.storage.sync.get('historyItems', function checkForHistoryItems(items) {
        var noHistoryItems = (typeof items.historyItems) === 'undefined';
        if (noHistoryItems) {
            chrome.storage.sync.set({
                historyItems: []
            });
        }
    });
}

function toggleTabToWindow() {
    getCurrentTab(function (tab) {
        var tabsIdsKey = 'tabsIds';
        var currTabIdStr = tab.id + '';
        var currTabId = tab.id;

        chrome.storage.local.get(tabsIdsKey, function (items) {
            var tabs = items[tabsIdsKey];
            var currTab = tabs[currTabIdStr];
            var savedTabs = tabs;

            var isCurrTabDetached = (typeof currTab) !== 'undefined';
            if (isCurrTabDetached) {
                chrome.tabs.move(currTabId, {
                    windowId: currTab.windowId,
                    index: currTab.index
                }, function (movedTab) {
                    chrome.tabs.update(movedTab.id, {active: true});
                });
                delete savedTabs[currTabIdStr];
            } else {
                chrome.windows.create({tabId: currTabId});
                savedTabs[currTabIdStr] = tab;
            }

            var options = {};
            options[tabsIdsKey] = savedTabs;
            chrome.storage.local.set(options);
        });
    });
}
