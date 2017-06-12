import React from 'react';
import Highlight from 'react-highlight';
import {ContentEditable} from 'components';

class ContentEditableBook extends React.Component {

  state = {
    text: 'Example of Content Editable'
  };

  handleTextChange(value) {
    this.setState({
      text: value
    });
  }

  render() {

    return (
      <div>

        <h4>Example</h4>

        <ContentEditable toolSize="small" value={this.state.text} onChange={this.handleTextChange.bind(this)}
                         validate="^[a-zA-Z ]+$"/>

        <h4>Code</h4>

        <Highlight className="html">
          {`<ContentEditable toolSize="small" value={this.state.text} onChange={this.handleTextChange.bind(this)} validate="^[a-zA-Z ]+$"/>`}
        </Highlight>

      </div>
    );
  }

}

export default ContentEditableBook;
