<div id="commentOutput" class="clearfix">
    <h3>Comments (<b data-bind="text: total"></b>)</h3>
    <div id="postComment" class="comment clearfix comment-post">
        <div class="comment-header">
        </div>

        <div class="comment-body">
            <textarea class="boxsizingBorder" data-bind="value: newComment().text" placeholder="Write your comment here."></textarea>
        </div>

        <div class="comment-footer pull-right">
            <div class="btn btn-primary btn-small" data-bind="click: create">post</div>
        </div>
    </div>
    <hr>
    <div id="commentDisplay">
        <div id="commentTools">
            <span>
                <select
                        data-bind="options: sortOptions, optionsText: 'text', value: selectedSort, event:{change: list}"></select>
            </span>
        </div>

        <div class="comment-list" data-bind="template:{name:'template-comment',foreach: comments}">
        </div>
    </div>
    <div data-bind="visible: showLoadMore" class="row-fluid">
        <div class="span12">
            <div class="btn span12" data-bind="click: more"><span class="h6">load more</span></div>
        </div>
    </div>
</div>
<script type="text/html" id="template-comment">
    <div class="comment-body">
        <div class="media" >
            <div class="pull-left" href="#" style="width: 16px; height: 16px" data-bind="css: { hide: !$data.parent()}"></div>
            <div class="media-body" >
                <b class="username media-heading" data-bind="text: displayName, visible: !!displayName"></b>
                <span data-bind="visible: !!dateCreated()">commented at <span data-bind="text: dateCreated.formattedDate"></span></span>
                <div data-bind="visible: !edit()">
                    <pre class="media" data-bind="html: text">
                    </pre>
                    <ul class="breadcrumb margin-bottom-five">
                        <li data-bind="visible: $root.isUserCommentOwner($data)"><a class="btn-link" data-bind="click: $root.edit">edit</a> <span class="divider">|</span></li>
                        <li data-bind="visible: $root.isUserCommentOwner($data)"><a class="btn-link" data-bind="click: $root.delete">delete</a> <span class="divider">|</span></li>
                        <li data-bind="visible: !showChildren() && children().length, click: $root.viewChildren" text="view replies"><a class="btn-link">
                            <i class="icon-comment"></i> show
                        </a><span class="divider">|</span></li>
                        <li data-bind="visible: showChildren() && children().length, click: $root.hideChildren" text="view replies"><a class="btn-link">
                            <i class="icon-comment"></i> hide
                        </a><span class="divider">|</span></li>
                        <li><a class="btn-link" data-bind="click: $root.reply">reply</a></li>
                    </ul>

                </div>
                <div data-bind="visible: edit">
                    <div class="comment clearfix comment-post">
                    <div>
                        <textarea class="boxsizingBorder" data-bind="value: text" placeholder="Write your comments here."></textarea>
                    </div>
                    <ul class="breadcrumb margin-bottom-five">
                        <li data-bind="visible: !$data.id()">
                            <a class="btn btn-small btn-primary" data-bind="click: $root.createChild">post</a> <!--<span class="divider">|</span>-->
                        </li>
                        <li data-bind="visible: !!$data.id()">
                            <a class="btn btn-small btn-primary" data-bind="click: $root.update">update</a> <!--<span class="divider">|</span>-->
                        </li>
                        <li><a class="btn btn-small" data-bind="click: $root.cancel">cancel</a></li>
                    </ul>
                    </div>
                </div>
                <div class="comment-children" data-bind="template:{name:'template-comment',foreach: children}, visible: showChildren">

                </div>
            </div>
        </div>
    </div>
</script>