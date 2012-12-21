var prefs = new gadgets.Prefs();

operationParameters = {
	  operationId: 'Document.PageProvider',
      operationParams: {
        pageSize: 5,
        query: "select * from Document where ecm:mixinType != 'HiddenInNavigation'"
      },
      operationContext: {},
      operationDocumentProperties: "common,dublincore",
      entityType: 'documents',
      usePagination: true,
      displayMethod: displayDocumentList,
      displayColumns: [
        {type: 'builtin', field: 'icon'},
        {type: 'builtin', field: 'titleWithLink', label: 'Title'},
        {type: 'date', field: 'dc:modified', label: 'Date'},
        {type: 'text', field: 'dc:creator', label: 'Author'}
      ],
      noEntryLabel: 'No documents'
};


gadgets.util.registerOnLoadHandler(function(){
	doAutomationRequest(operationParameters); // invoke operation when gadget is loaded
});


