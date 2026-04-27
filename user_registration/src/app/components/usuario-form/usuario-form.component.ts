import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './usuario-form.component.html',
  styleUrl: './usuario-form.component.scss',
})
export class UsuarioFormComponent implements OnInit {
  form: FormGroup;
  mensagemSucesso = '';
  mensagemErro = '';
  carregando = false;
  usuarios: Usuario[] = [];
  usuarioParaExcluir: Usuario | null = null;
  modoEdicao = false;
  idUsuarioEmEdicao: number | null = null;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService
  ) {
    this.form = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    });
  }

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.usuarioService.listar().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
      },
      error: () => {
        this.mensagemErro = 'Erro ao carregar a lista de usuários.';
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.limparMensagens();

    if (this.modoEdicao && this.idUsuarioEmEdicao !== null) {
      this.atualizarUsuario();
    } else {
      this.criarUsuario();
    }
  }

  private criarUsuario(): void {
    this.carregando = true;
    const usuario: Usuario = this.form.value;

    this.usuarioService.cadastrar(usuario).subscribe({
      next: () => {
        this.mensagemSucesso = 'Usuário cadastrado com sucesso!';
        this.form.reset();
        this.carregarUsuarios();
        this.carregando = false;
      },
      error: (err) => {
        this.mensagemErro = err.error?.message || 'Erro ao cadastrar usuário.';
        this.carregando = false;
      },
    });
  }

  private atualizarUsuario(): void {
    if (this.idUsuarioEmEdicao === null) {
      return;
    }

    this.carregando = true;
    const usuario: Usuario = this.form.value;

    this.usuarioService.atualizarUsuario(this.idUsuarioEmEdicao, usuario).subscribe({
      next: () => {
        this.mensagemSucesso = 'Usuário atualizado com sucesso!';
        this.form.reset();
        this.modoEdicao = false;
        this.idUsuarioEmEdicao = null;
        this.carregarUsuarios();
        this.carregando = false;
      },
      error: (err) => {
        this.mensagemErro = err.error?.message || 'Erro ao atualizar usuário.';
        this.carregando = false;
      },
    });
  }

  private limparMensagens(): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';
  }

  prepararEdicao(usuario: Usuario): void {
    this.modoEdicao = true;
    this.idUsuarioEmEdicao = usuario.id ?? null;
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    this.form.patchValue({
      nome: usuario.nome,
      email: usuario.email,
      cep: usuario.cep ?? '',
    });
    this.form.markAllAsTouched();
  }

  abrirConfirmacaoExclusao(usuario: Usuario): void {
    if (usuario.id == null) {
      return;
    }
    this.mensagemSucesso = '';
    this.mensagemErro = '';
    this.usuarioParaExcluir = usuario;
  }

  fecharConfirmacaoExclusao(): void {
    this.usuarioParaExcluir = null;
  }

  cancelarEdicao(): void {
    this.modoEdicao = false;
    this.idUsuarioEmEdicao = null;
    this.form.reset();
    this.mensagemSucesso = '';
    this.mensagemErro = '';
  }

  confirmarExclusao(): void {
    const id = this.usuarioParaExcluir?.id;
    if (id == null) {
      return;
    }

    this.usuarioParaExcluir = null;
    this.mensagemSucesso = '';
    this.mensagemErro = '';
    this.usuarioService.excluir(id).subscribe({
      next: () => {
        this.mensagemSucesso = 'Usuário excluído com sucesso.';
        this.carregarUsuarios();
      },
      error: (err) => {
        this.mensagemErro =
          err.status === 404
            ? 'Usuário não encontrado.'
            : 'Não foi possível concluir a exclusão no servidor.';
        this.carregarUsuarios();
      },
    });
  }
}
