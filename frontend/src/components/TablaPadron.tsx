import { useEffect, useState } from 'react'
import { Pencil, Trash2, ChevronLeft, ChevronRight } from 'lucide-react'

interface PadronElectoral {
    id: number
    cui: string
    nombre: string
    apellido: string
    correo: string | null

    departamentoId: number | null
    departamento: string

    municipioId: number | null
    municipio: string

    centroVotacionId: number | null
    centroVotacion: string | null

    mesaVotacionId: number | null
    mesaVotacion: string | null

    numeroLista: number | null
    hoja: number | null
    linea: number | null
    estado: string
}

interface TablaPadronProps {
    registros: PadronElectoral[]
    loading: boolean
    onRegistroEliminado: () => void
    onEditar: (registro: PadronElectoral) => void
}

export default function TablaPadron({
    registros,
    loading,
    onRegistroEliminado,
    onEditar
}: TablaPadronProps) {
    const [paginaActual, setPaginaActual] = useState(1)

    const registrosPorPagina = 20

    const registrosOrdenados = [...registros].sort((a, b) => {
        const mesaA = a.mesaVotacionId ?? Number.MAX_SAFE_INTEGER
        const mesaB = b.mesaVotacionId ?? Number.MAX_SAFE_INTEGER

        if (mesaA !== mesaB) {
            return mesaA - mesaB
        }

        const listaA = a.numeroLista ?? Number.MAX_SAFE_INTEGER
        const listaB = b.numeroLista ?? Number.MAX_SAFE_INTEGER

        return listaA - listaB
    })

    const totalPaginas = Math.max(
        1,
        Math.ceil(registrosOrdenados.length / registrosPorPagina)
    )

    const indiceInicial =
        (paginaActual - 1) * registrosPorPagina

    const indiceFinal =
        indiceInicial + registrosPorPagina

    const registrosPagina =
        registrosOrdenados.slice(indiceInicial, indiceFinal)

    useEffect(() => {
        if (paginaActual > totalPaginas) {
            setPaginaActual(totalPaginas)
        }
    }, [paginaActual, totalPaginas])

    const handleEliminar = async (id: number) => {
        const confirmar = window.confirm(
            '¿Estás seguro de que deseas eliminar este registro?'
        )

        if (!confirmar) {
            return
        }

        try {
            const res = await fetch(
                `/api/padron-electoral/${id}`,
                {
                    method: 'DELETE'
                }
            )

            if (!res.ok) {
                const contenido = await res.text()

                throw new Error(
                    contenido || 'Error al eliminar el registro'
                )
            }

            onRegistroEliminado()
        } catch (error) {
            const mensaje =
                error instanceof Error
                    ? error.message
                    : 'Error al eliminar el registro'

            alert(mensaje)
        }
    }

    if (loading) {
        return (
            <div className="bg-white rounded-lg shadow p-6 text-center text-gray-600">
                Cargando registros...
            </div>
        )
    }



    return (
        <div className="bg-white rounded-lg shadow overflow-hidden animate-fade-in">
            <div className="overflow-x-auto">
                <table className="w-full min-w-[1500px]">
                    <thead>
                        <tr className="bg-gray-100 border-b border-gray-200">
                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                No.
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                CUI
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Nombre
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Apellido
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Correo
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Departamento
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Municipio
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Centro
                            </th>

                            <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                                Mesa
                            </th>

                            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">
                                Lista
                            </th>

                            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">
                                Hoja
                            </th>

                            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">
                                Línea
                            </th>

                            <th className="px-4 py-3 text-center text-sm font-semibold text-gray-900">
                                Acción
                            </th>
                        </tr>
                    </thead>

                    <tbody>
                        {registrosPagina.length === 0 ? (
                            <tr>
                                <td
                                    colSpan={13}
                                    className="px-6 py-8 text-center text-gray-500"
                                >
                                    No hay registros
                                </td>
                            </tr>
                        ) : (
                            registrosPagina.map((registro, index) => (
                                <tr
                                    key={registro.id}
                                    className="border-b border-gray-200 hover:bg-gray-50"
                                >
                                    <td className="px-4 py-4 text-sm font-semibold text-gray-900">
                                        {indiceInicial + index + 1}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.cui}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.nombre}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.apellido}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.correo || 'Sin correo'}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.departamento}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.municipio}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.centroVotacion || 'Sin asignar'}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-gray-900">
                                        {registro.mesaVotacion || 'Sin asignar'}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-center font-medium text-gray-900">
                                        {registro.numeroLista ?? '-'}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-center text-gray-900">
                                        {registro.hoja ?? '-'}
                                    </td>

                                    <td className="px-4 py-4 text-sm text-center text-gray-900">
                                        {registro.linea ?? '-'}
                                    </td>

                                    <td className="px-4 py-4 text-sm">
                                        <div className="flex items-center justify-center gap-2">
                                            <button
                                                type="button"
                                                onClick={() => onEditar(registro)}
                                                className="inline-flex items-center gap-1 px-3 py-2 text-blue-600 hover:bg-blue-50 rounded transition"
                                                title="Editar registro"
                                            >
                                                <Pencil size={16} />
                                                Editar
                                            </button>

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    handleEliminar(registro.id)
                                                }
                                                className="inline-flex items-center gap-1 px-3 py-2 text-red-600 hover:bg-red-50 rounded transition"
                                                title="Eliminar registro"
                                            >
                                                <Trash2 size={16} />
                                                Eliminar
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-6 py-4 bg-gray-50 border-t border-gray-200">
                <div className="text-sm text-gray-600">
                    {registrosOrdenados.length === 0
                        ? 'No hay registros'
                        : `Mostrando ${indiceInicial + 1} a ${Math.min(
                            indiceFinal,
                            registrosOrdenados.length
                        )} de ${registrosOrdenados.length} registros`}
                </div>

                <div className="flex items-center gap-3">
                    <button
                        type="button"
                        onClick={() =>
                            setPaginaActual((paginaAnterior) =>
                                Math.max(paginaAnterior - 1, 1)
                            )
                        }
                        disabled={paginaActual === 1}
                        className="inline-flex items-center gap-1 px-3 py-2 border border-gray-300 rounded-lg text-sm hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
                    >
                        <ChevronLeft size={16} />
                        Anterior
                    </button>

                    <span className="text-sm font-medium text-gray-700">
                        Página {paginaActual} de {totalPaginas}
                    </span>

                    <button
                        type="button"
                        onClick={() =>
                            setPaginaActual((paginaAnterior) =>
                                Math.min(
                                    paginaAnterior + 1,
                                    totalPaginas
                                )
                            )
                        }
                        disabled={paginaActual === totalPaginas}
                        className="inline-flex items-center gap-1 px-3 py-2 border border-gray-300 rounded-lg text-sm hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
                    >
                        Siguiente
                        <ChevronRight size={16} />
                    </button>
                </div>
            </div>
        </div>
    )

}