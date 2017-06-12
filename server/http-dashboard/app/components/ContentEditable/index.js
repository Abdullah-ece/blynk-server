import React from 'react';

import {Button} from 'antd';

import classnames from 'classnames';

import ContentEditable from './components/ContentEditable';

import './styles.less';

class Editable extends React.Component {

  static propTypes = {
    value: React.PropTypes.string,
    onChange: React.PropTypes.func,
    validate: React.PropTypes.string,
    className: React.PropTypes.string,
    toolSize: React.PropTypes.string
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

  validate() {
    if (!this.props.validate) return true;

    return new RegExp(this.props.validate).test(this.prepareForSave(this.state.html));
  }

  startToEdit() {
    this.setState({isEditable: true});
  }

  cancelEdit() {
    this.setState({
      isEditable: false,
      html: this.defaultHtml
    });
  }

  prepareForSave(value) {
    return value.replace(/&nbsp;/g, ' ')
      .replace(/&.*;/g, '')
      .trim();
  }

  saveEdit() {

    if (!this.validate())
      return this.cancelEdit();

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

    const className = classnames({
        'content-editable': true,
        'content-editable--not-valid': !this.validate()
      }) + (this.props.className || '');

    return (
      <div className={className}>
        { !this.state.isEditable &&
        <div>
          <span>{ this.state.html }</span> <Button size={this.props.toolSize || 'default'} icon="edit"
                                                   className="user-profile--my-account-edit-button"
                                                   onClick={this.startToEdit.bind(this)}/>
        </div>
        }
        { this.state.isEditable &&
        <div>
          <ContentEditable html={this.state.html}
                           regexp={this.state.regexp}
                           onBlur={this.handleBlur.bind(this)}
                           onChange={this.handleChange.bind(this)}
                           onEnterPress={this.handleEnterPress.bind(this)}/>&nbsp;
          <Button icon="save" size={this.props.toolSize || 'default'} className="user-profile--my-account-edit-button"
                  onClick={this.saveEdit.bind(this)} disabled={!this.validate()}/>&nbsp;
          <Button icon="close" size={this.props.toolSize || 'default'} className="user-profile--my-account-edit-button"
                  onClick={this.cancelEdit.bind(this)}/>
        </div>
        }
      </div>
    );
  }

}

export default Editable;
