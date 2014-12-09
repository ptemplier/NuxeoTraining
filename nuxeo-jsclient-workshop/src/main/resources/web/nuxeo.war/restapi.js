var RestAPI = {};

////////////////////////////// EXERCISE 1 - NUXEO JS CONFIGURATION

// Configure Nuxeo Client
RestAPI.config = function () {
  //Instantiate Nuxeo Client below
  
  
  // Make sure:
  // - That the client will return the dublincore and file schemas
  // - To return the client afterwards
  
}

////////////////////////////// EXERCISE 2 - CURRENT USER

// Use a GET request to get the user: Administrator
// You can use the client using this.client.doSomething
// Callback: callbackCurrentUser
RestAPI.getCurrentUser = function () {
  
}

////////////////////////////// EXERCISE 3 - EXECUTE QUERY

// Use a GET request to execute the query typed
// Make sure to include the NXQLQuery parameter in the called URL
// Hint: to do that you will need to build a request var, 
// and add it your parameter afterwards as a JSON object
// Callback: callbackQuery
RestAPI.executeQuery = function (NXQLQuery) {
  
}

////////////////////////////// EXERCISE 4 - DISPLAY WORKSPACE CHILDREN

// Use the high level document API to get the "documentRoot" children
// Callback: callbackRootChildren
var documentRoot = "/default-domain/workspaces/Meetings";
RestAPI.getRootChildren = function () {
  
}


////////////////////////////// EXERCISE 5 - DISPLAY DOCUMENT PROPERTIES

// Use the high level document API to get a document using its id 
// Callback: callbackFetchDocument
RestAPI.fetchDocument = function (id) {
  
}

////////////////////////////// EXERCISE 6 - UPDATE DOCUMENT

// Use the high level document API to update the current document
// And save it afterwards
// Callback: callbackUpdateDocument
RestAPI.updateDocument = function (map) {
  
}

////////////////////////////// EXERCISE 7 - CREATE DOCUMENT

// Use the high level document API to create a document
// Form data can be accessed through map["schema-prefix:metadata"]
// e.g.: map["dc:title"]
// Form fields:
// dc:title
// dc:description
// dc:language
// dc:nature
// Callback: callbackCreateDocument
var creationRoot = "/default-domain/workspaces/Meetings";
RestAPI.createDocument = function (map) {
  
}

////////////////////////////// EXERCISE 8 - DELETE DOCUMENT

// Use the high level document API to delete the current document
// Callback: callbackDeleteDocument
RestAPI.deleteDocument = function () {
  
}

////////////////////////////// EXERCISE 9 - FILE IMPORT

// Use the uploader API to import a file
// Operation to call: FileManager.Import (Create document from file)
// Also see http://doc.nuxeo.com/x/IYwZAQ to customize the document type created
// Callback: callbackImportFile
var importRoot = "/default-domain/workspaces/Meetings";
RestAPI.importFile = function (file) {
  
}

////////////////////////////// EXERCISE 10 - ATTACH BLOB

// Use the uploader API to attach a file in an existing document
// File is to be saved into the "file:content" xpath
// Operation to call: Blob.Attach
// Callback: callbackAttachBlob
RestAPI.attachBlob = function (file) {
  
}

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// DO NOT EDIT THE CODE BELOW //////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

function callbackCreateDocument(error, file) {
  if (error) {
    throwError("Cannot create document -> " + error);
    throw error;
  }
  console.log('Created ' + file.title + ' file')
  location.reload();
}

function importFiles(files) {
  for (var i = 0; i < files.length; i++) {
    RestAPI.importFile(files[i]);
  }
}

function callbackDeleteDocument(error, data) {
  $('#modalDelete').modal('hide');
  if (error) {
    throwError("Cannot delete document -> " + error);
    throw error;
  }
  location.reload();
  console.log("Document has been deleted");
}

function callbackAttachBlob(error, data) {
  $('#modalBlob').modal('hide');
  if (error) {
    throwError("Cannot attach blob -> " + error);
    throw error;
  }
  RestAPI.fetchDocument(RestAPI.currentDocument.uid);
  console.log("Blob has been attached");
}

function callbackImportFile(error, data) {
  if (error) {
    throwError("Cannot import files -> " + error);
    throw error;
  }
  location.reload();
  console.log("Files have been imported");
}

function save() {
  var map = new Object();
  $("#docForm :input").each(function () {
    var name = $(this).attr('name');
    var input = $(this).val();
    map[name] = input;
  });
  RestAPI.updateDocument(map);
}

function callbackCurrentUser(error, user) {
  if (error) {
    throwError("Cannot get user -> " + error);
    throw error;
  }
  console.log(user);
  var template = $('#userId').html();
  Mustache.parse(template);
  var rendered = Mustache.render(template, {userId: user.id});
  $('#targetUserId').html(rendered);
  template = $('#userInfos').html();
  Mustache.parse(template);
  rendered = Mustache.render(template, {user: user});
  $('#targetUserInfos').html(rendered);
}

function callbackRootChildren(error, children) {
  if (error) {
    throwError("Cannot fetch documents -> " + error);
    throw error;
  }
  console.log('Root document has ' + children.entries.length + ' children');
  var template = $('#children').html();
  Mustache.parse(template);
  var rendered = Mustache.render(template, {children: children.entries});
  $('#targetChildren').html(rendered);
}

function callbackQuery(error, result) {
  if (error) {
    throwError("Cannot run query -> " + error);
    throw error;
  }
  console.log('Root document has ' + result.entries.length + ' children');
  var template = $('#children').html();
  Mustache.parse(template);
  var rendered = Mustache.render(template, {children: result.entries});
  $('#targetChildren').html(rendered);
}

function callbackFetchDocument(error, doc) {
  if (error) {
    throwError("Cannot display document metadata -> " + error);
    throw error;
  }
  RestAPI.currentDocument = doc;
  if (doc.contextParameters.acls != undefined) {
    var ace = doc.contextParameters.acls[0].ace;
  }
  console.log('Selecting ' + doc.title);
  var template = $('#displayMetadata').html();
  Mustache.parse(template);
  var rendered = Mustache.render(template, {doc: doc, ace: ace});
  $('#displayForm').html(rendered);
  template = $('#editionMetadata').html();
  Mustache.parse(template);
  rendered = Mustache.render(template, {doc: doc});
  $('#editionForm').html(rendered);
}

function callbackUpdateDocument(error, doc) {
  if (error) {
    throwError("Cannot display document metadata -> " + error);
    throw error;
  }
  RestAPI.currentDocument = doc;
  if (doc.contextParameters.acls != undefined) {
    var ace = doc.contextParameters.acls[0].ace;
  }
  console.log('Selecting ' + doc.title);
  var template = $('#displayMetadata').html();
  Mustache.parse(template);
  var rendered = Mustache.render(template, {doc: doc, ace: ace});
  $('#displayForm').html(rendered);
  template = $('#editionMetadata').html();
  Mustache.parse(template);
  rendered = Mustache.render(template, {doc: doc});
  $('#editionForm').html(rendered);
  toggle();
}

// Show modal for error
function throwError(error) {
  $('#errorValue').append(error);
  $('#modalError').modal('show');
}

// Show modal confirm of deletion
function displayDelete() {
  $('#modalDelete').modal('show');
}

// Show modal for attaching blob
function displayBlob() {
  $('#modalBlob').modal('show');
}

// Toggle edit form
function toggle() {
  $('#editionForm').toggle();
  $('#displayForm').toggle();
}

$(document)
  .ready(function () {
    //Build Nuxeo Client
    RestAPI.client = RestAPI.config();

    // Call and display default domain children
    RestAPI.getRootChildren();

    // Call and display current user
    RestAPI.getCurrentUser();

    // Call query when clicking on search
    $('#search').click(function () {
      RestAPI.executeQuery($('#searchInput').val());
    });

    $('#importAction').click(function () {
      $('#modalImport').modal('show');
    });

    $('#importFiles').click(function () {
      console.log("Files size: " + $('#files')[0].files.length);
      importFiles($('#files')[0].files);
    });

    $('attachBlob').click(function () {
      $('#blobModal').modal('show');
    })

    $('#importBlob').click(function () {
      RestAPI.attachBlob($('#blob')[0].files[0]);
    });

    $('#refresh').click(function () {
      location.reload();
    });

    $('#deleteDoc').click(function () {
      RestAPI.deleteDocument();
    });

    $('#create').click(function () {
      $('#modalCreate').modal('show');
    });

    $('#createDoc').click(function () {
      var map = new Object();
      $("#createDocForm :input").each(function () {
        var name = $(this).attr('name');
        var input = $(this).val();
        map[name] = input;
      });
      RestAPI.createDocument(map);
    });

  }
)
;
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// DO NOT EDIT THE CODE ABOVE //////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
