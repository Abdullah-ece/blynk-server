import React from 'react';

import {Button} from 'antd';

import ContentEditable from './components/ContentEditable';

class Editable extends React.Component {

  static propTypes = {
    value: React.PropTypes.string,
    onChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.defaultHtml = props.value;

    this.state = {
      html: props.value,
      isEditable: false
    };
  }

  componentWillReceiveProps(props) {
    this.setState({
      html: props.value
    });
  }

  startToEdit() {
    this.setState({isEditable: true});
  }

  cancelEdit() {
    this.setState({isEditable: false});
    this.setState({html: this.defaultHtml});
  }

  prepareForSave(value) {
    return value.replace(/&nbsp;/g, ' ')
      .replace(/&.*;/g, '')
      .trim();
  }

  saveEdit() {

    let html = this.prepareForSave(this.state.html);

    if (!html) {
      this.cancelEdit();
    } else {
      this.setState({
        isEditable: false,
        html: html
      });
      this.props.onChange(html);
      this.defaultHtml = html;
    }

  }

  handleChange(event) {
    this.setState({
      html: event.target.value
    });
  }

  handleBlur(event) {
    if (!event.target.value) {
      this.cancelEdit();
    }
  }

  handleEnterPress(event) {
    if (!event.target.value) {
      this.cancelEdit();
    } else {
      this.saveEdit();
    }
  }

  render() {
    return (
      <div className="user-profile--editable">
        { !this.state.isEditable &&
        <div>
          <span>{ this.state.html }</span> <Button icon="edit" className="user-profile--my-account-edit-button"
                                                   onClick={this.startToEdit.bind(this)}/>
        </div>
        }
        { this.state.isEditable &&
        <div>
          <ContentEditable html={this.state.html}
                           onBlur={this.handleBlur.bind(this)}
                           onChange={this.handleChange.bind(this)}
                           onEnterPress={this.handleEnterPress.bind(this)}/>&nbsp;
          <Button icon="save" className="user-profile--my-account-edit-button"
                  onClick={this.saveEdit.bind(this)}/>&nbsp;
          <Button icon="close" className="user-profile--my-account-edit-button" onClick={this.cancelEdit.bind(this)}/>
        </div>
        }
      </div>
    );
  }

}

export default Editable;
